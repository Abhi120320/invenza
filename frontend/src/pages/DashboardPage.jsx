import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { dashboardApi, warehouseApi, productApi } from '../api/client';
import {
  ArrowDownToLine, ArrowUpFromLine, ArrowRightLeft,
  Filter, Package, AlertTriangle, SlidersHorizontal, RefreshCw, CheckCircle2
} from 'lucide-react';

export default function DashboardPage() {
  const navigate = useNavigate();

  const [filters, setFilters] = useState({
    documentType: 'all',
    status: 'all',
    warehouse: 'all',
    category: 'all',
  });

  const [warehouses, setWarehouses] = useState([]);
  const [categories, setCategories] = useState([]);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  // Load filter options on mount
  useEffect(() => {
    Promise.allSettled([
      warehouseApi.list(),
      productApi.listCategories()
    ]).then(([wRes, cRes]) => {
      if (wRes.status === 'fulfilled' && Array.isArray(wRes.value?.data)) {
        setWarehouses(wRes.value.data);
      }
      if (cRes.status === 'fulfilled' && Array.isArray(cRes.value?.data)) {
        setCategories(cRes.value.data);
      }
    });
  }, []);

  const fetchStats = useCallback(async (isSilent = false) => {
    if (!isSilent) setRefreshing(true);
    try {
      const params = {};
      if (filters.warehouse && filters.warehouse !== 'all') {
        params.warehouse_id = filters.warehouse;
      }
      if (filters.category && filters.category !== 'all') {
        params.category_id = filters.category;
      }
      if (filters.status && filters.status !== 'all') {
        params.status = filters.status.toUpperCase();
      }
      if (filters.documentType && filters.documentType !== 'all') {
        params.document_type = filters.documentType;
      }

      const { data } = await dashboardApi.kpis(params);
      setStats(data);
    } catch (err) {
      console.error('Failed to fetch dashboard stats', err);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, [filters]);

  // Fetch when filters change
  useEffect(() => {
    fetchStats();
  }, [fetchStats]);

  // Live Auto-Refresh (WebSocket + Polling fallback)
  useEffect(() => {
    let ws = null;
    try {
      const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
      const host = window.location.hostname;
      const port = '8000'; // backend port
      ws = new WebSocket(`${protocol}//${host}:${port}/ws/dashboard`);

      ws.onmessage = () => {
        fetchStats(true);
      };

      ws.onerror = () => {
        // Fallback handled by interval
      };
    } catch (e) {
      // Ignore WS error and rely on interval
    }

    const interval = setInterval(() => {
      fetchStats(true);
    }, 4000);

    return () => {
      if (ws) ws.close();
      clearInterval(interval);
    };
  }, [fetchStats]);

  const getCardMetric = (opStats, defaultAction) => {
    if (!opStats) return { count: 0, label: defaultAction };

    if (filters.status === 'done') {
      return { count: opStats.done ?? 0, label: 'completed' };
    }
    if (filters.status === 'ready') {
      return { count: opStats.ready ?? 0, label: 'ready to process' };
    }
    if (filters.status === 'waiting') {
      return { count: opStats.waiting ?? 0, label: 'waiting' };
    }
    if (filters.status === 'draft') {
      return { count: opStats.draft ?? 0, label: 'drafts' };
    }
    if (filters.status === 'canceled') {
      return { count: opStats.canceled ?? 0, label: 'canceled' };
    }

    // Default 'all'
    if (opStats.to_do > 0) {
      return { count: opStats.to_do, label: defaultAction };
    }
    if (opStats.done > 0 && opStats.to_do === 0) {
      return { count: opStats.done, label: 'completed' };
    }
    return { count: 0, label: defaultAction };
  };

  const showAll = filters.documentType === 'all';
  const showReceipts = showAll || filters.documentType === 'receipt';
  const showDeliveries = showAll || filters.documentType === 'delivery';
  const showTransfers = showAll || filters.documentType === 'internal';
  const showAdjustments = showAll || filters.documentType === 'adjustment';

  const receiptMetric = getCardMetric(stats?.receipts, 'to receive');
  const deliveryMetric = getCardMetric(stats?.deliveries, 'to deliver');
  const transferMetric = getCardMetric(stats?.transfers, 'scheduled');
  const adjustmentMetric = getCardMetric(stats?.adjustments, 'pending');

  const totalRegisteredProducts = stats?.total_products ?? 0;
  const inStockProducts = stats?.total_products_in_stock ?? 0;
  const lowStockItems = stats?.low_stock_items ?? stats?.low_stock_count ?? 0;

  return (
    <div>
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h1 className="page-title">Dashboard</h1>
          <p className="page-subtitle">Overview of your inventory operations</p>
        </div>
        <button
          className="btn btn-secondary btn-sm"
          onClick={() => fetchStats(false)}
          disabled={refreshing}
          style={{ display: 'flex', alignItems: 'center', gap: '6px' }}
        >
          <RefreshCw size={14} className={refreshing ? 'animate-spin' : ''} />
          {refreshing ? 'Refreshing...' : 'Refresh'}
        </button>
      </div>

      {/* ── Filters ── */}
      <div className="card" style={{ marginBottom: 'var(--space-6)', padding: 'var(--space-4)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-4)', flexWrap: 'wrap' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 6, color: 'var(--text-muted)', fontWeight: 600, fontSize: '0.875rem' }}>
            <Filter size={16} /> Filters
          </div>

          <select
            className="form-select"
            style={{ width: 'auto', minWidth: 160 }}
            value={filters.documentType}
            onChange={(e) => setFilters({ ...filters, documentType: e.target.value })}
          >
            <option value="all">All Document Types</option>
            <option value="receipt">Receipts</option>
            <option value="delivery">Deliveries</option>
            <option value="internal">Internal Transfers</option>
            <option value="adjustment">Adjustments</option>
          </select>

          <select
            className="form-select"
            style={{ width: 'auto', minWidth: 160 }}
            value={filters.status}
            onChange={(e) => setFilters({ ...filters, status: e.target.value })}
          >
            <option value="all">All Statuses</option>
            <option value="draft">Draft</option>
            <option value="waiting">Waiting</option>
            <option value="ready">Ready</option>
            <option value="done">Done</option>
            <option value="canceled">Canceled</option>
          </select>

          <select
            className="form-select"
            style={{ width: 'auto', minWidth: 180 }}
            value={filters.warehouse}
            onChange={(e) => setFilters({ ...filters, warehouse: e.target.value })}
          >
            <option value="all">All Warehouses</option>
            {warehouses.map((w) => (
              <option key={w.id} value={w.id}>
                {w.name} {w.short_code ? `(${w.short_code})` : ''}
              </option>
            ))}
          </select>

          <select
            className="form-select"
            style={{ width: 'auto', minWidth: 180 }}
            value={filters.category}
            onChange={(e) => setFilters({ ...filters, category: e.target.value })}
          >
            <option value="all">All Categories</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name}
              </option>
            ))}
          </select>

          {(filters.documentType !== 'all' || filters.status !== 'all' || filters.warehouse !== 'all' || filters.category !== 'all') && (
            <button
              className="btn btn-ghost btn-sm"
              onClick={() => setFilters({ documentType: 'all', status: 'all', warehouse: 'all', category: 'all' })}
              style={{ fontSize: '0.8rem', color: 'var(--brand-primary)' }}
            >
              Reset Filters
            </button>
          )}
        </div>
      </div>

      {/* ── Summary Cards ── */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: 'var(--space-8)' }}>
          <div className="spinner" />
        </div>
      ) : (
        <>
          {/* Top KPIs */}
          <div className="grid-2" style={{ marginBottom: 'var(--space-6)' }}>
            <div
              className="card hover-card"
              style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-4)', padding: 'var(--space-4)', cursor: 'pointer' }}
              onClick={() => navigate('/products')}
            >
              <div style={{ width: 48, height: 48, borderRadius: 'var(--radius-full)', background: 'hsla(168,85%,48%,0.1)', color: 'var(--brand-primary)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Package size={24} />
              </div>
              <div>
                <div style={{ fontSize: '2rem', fontWeight: 700, lineHeight: 1 }}>
                  {inStockProducts > 0 ? inStockProducts : totalRegisteredProducts}
                </div>
                <div style={{ color: 'var(--text-secondary)', fontWeight: 500, marginTop: '4px' }}>
                  {inStockProducts > 0 ? 'Total Products in Stock' : 'Total Products Registered'}
                </div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '2px' }}>
                  {totalRegisteredProducts} total product definitions
                </div>
              </div>
            </div>

            <div
              className="card hover-card"
              style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-4)', padding: 'var(--space-4)', cursor: 'pointer' }}
              onClick={() => navigate('/stock')}
            >
              <div style={{ width: 48, height: 48, borderRadius: 'var(--radius-full)', background: 'hsla(3,100%,61%,0.1)', color: 'var(--color-error)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <AlertTriangle size={24} />
              </div>
              <div>
                <div style={{ fontSize: '2rem', fontWeight: 700, lineHeight: 1, color: lowStockItems > 0 ? 'var(--color-error)' : 'inherit' }}>
                  {lowStockItems}
                </div>
                <div style={{ color: 'var(--text-secondary)', fontWeight: 500, marginTop: '4px' }}>Low / Out of Stock Items</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '2px' }}>
                  {stats?.out_of_stock_count || 0} items with 0 on-hand
                </div>
              </div>
            </div>
          </div>

          {/* Operation Cards */}
          <div style={{
            display: 'grid',
            gridTemplateColumns: showAll ? 'repeat(auto-fit, minmax(280px, 1fr))' : '1fr',
            gap: 'var(--space-6)',
          }}>
            {/* Receipt Card */}
            {showReceipts && (
              <div
                className="card hover-card"
                style={{ cursor: 'pointer', transition: 'all 0.2s', display: 'flex', flexDirection: 'column' }}
                onClick={() => navigate('/receipts')}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-3)', marginBottom: 'var(--space-6)' }}>
                  <div style={{
                    width: 40, height: 40, borderRadius: 'var(--radius-md)',
                    background: 'hsla(168,85%,48%,0.15)', color: 'var(--brand-primary)',
                    display: 'flex', alignItems: 'center', justifyContent: 'center'
                  }}>
                    <ArrowDownToLine size={20} />
                  </div>
                  <h2 style={{ fontSize: '1.25rem', fontWeight: 600 }}>Receipts</h2>
                </div>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: '3rem', fontWeight: 800, lineHeight: 1, marginBottom: 'var(--space-2)' }}>
                    {receiptMetric.count}
                  </div>
                  <div style={{ fontSize: '1.125rem', color: 'var(--text-secondary)', fontWeight: 500, marginBottom: 'var(--space-6)' }}>
                    {receiptMetric.label}
                  </div>
                </div>
                <div style={{ display: 'flex', gap: 'var(--space-4)', borderTop: '1px solid var(--border-subtle)', paddingTop: 'var(--space-4)' }}>
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--color-success)' }}>
                      {stats?.receipts?.done ?? 0}
                    </div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Done</div>
                  </div>
                  <div style={{ width: 1, background: 'var(--border-subtle)' }} />
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: '1.2rem', fontWeight: 700, color: (stats?.receipts?.late ?? 0) > 0 ? 'var(--color-error)' : 'var(--text-primary)' }}>
                      {stats?.receipts?.late ?? 0}
                    </div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Late</div>
                  </div>
                  <div style={{ width: 1, background: 'var(--border-subtle)' }} />
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: '1.2rem', fontWeight: 700 }}>
                      {stats?.receipts?.total_operations ?? 0}
                    </div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Total</div>
                  </div>
                </div>
              </div>
            )}

            {/* Delivery Card */}
            {showDeliveries && (
              <div
                className="card hover-card"
                style={{ cursor: 'pointer', transition: 'all 0.2s', display: 'flex', flexDirection: 'column' }}
                onClick={() => navigate('/deliveries')}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-3)', marginBottom: 'var(--space-6)' }}>
                  <div style={{
                    width: 40, height: 40, borderRadius: 'var(--radius-md)',
                    background: 'hsla(152,70%,48%,0.15)', color: 'var(--color-success)',
                    display: 'flex', alignItems: 'center', justifyContent: 'center'
                  }}>
                    <ArrowUpFromLine size={20} />
                  </div>
                  <h2 style={{ fontSize: '1.25rem', fontWeight: 600 }}>Deliveries</h2>
                </div>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: '3rem', fontWeight: 800, lineHeight: 1, marginBottom: 'var(--space-2)' }}>
                    {deliveryMetric.count}
                  </div>
                  <div style={{ fontSize: '1.125rem', color: 'var(--text-secondary)', fontWeight: 500, marginBottom: 'var(--space-6)' }}>
                    {deliveryMetric.label}
                  </div>
                </div>
                <div style={{ display: 'flex', gap: 'var(--space-4)', borderTop: '1px solid var(--border-subtle)', paddingTop: 'var(--space-4)' }}>
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--color-success)' }}>
                      {stats?.deliveries?.done ?? 0}
                    </div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Done</div>
                  </div>
                  <div style={{ width: 1, background: 'var(--border-subtle)' }} />
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: '1.2rem', fontWeight: 700, color: (stats?.deliveries?.late ?? 0) > 0 ? 'var(--color-error)' : 'var(--text-primary)' }}>
                      {stats?.deliveries?.late ?? 0}
                    </div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Late</div>
                  </div>
                  <div style={{ width: 1, background: 'var(--border-subtle)' }} />
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: '1.2rem', fontWeight: 700 }}>
                      {stats?.deliveries?.total_operations ?? 0}
                    </div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Total</div>
                  </div>
                </div>
              </div>
            )}

            {/* Internal Transfer Card */}
            {showTransfers && (
              <div
                className="card hover-card"
                style={{ cursor: 'pointer', transition: 'all 0.2s', display: 'flex', flexDirection: 'column' }}
                onClick={() => navigate('/transfers')}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-3)', marginBottom: 'var(--space-6)' }}>
                  <div style={{
                    width: 40, height: 40, borderRadius: 'var(--radius-md)',
                    background: 'hsla(45,100%,51%,0.15)', color: 'var(--color-warning)',
                    display: 'flex', alignItems: 'center', justifyContent: 'center'
                  }}>
                    <ArrowRightLeft size={20} />
                  </div>
                  <h2 style={{ fontSize: '1.25rem', fontWeight: 600 }}>Internal Transfers</h2>
                </div>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: '3rem', fontWeight: 800, lineHeight: 1, marginBottom: 'var(--space-2)' }}>
                    {transferMetric.count}
                  </div>
                  <div style={{ fontSize: '1.125rem', color: 'var(--text-secondary)', fontWeight: 500, marginBottom: 'var(--space-6)' }}>
                    {transferMetric.label}
                  </div>
                </div>
                <div style={{ display: 'flex', gap: 'var(--space-4)', borderTop: '1px solid var(--border-subtle)', paddingTop: 'var(--space-4)' }}>
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--color-success)' }}>
                      {stats?.transfers?.done ?? 0}
                    </div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Done</div>
                  </div>
                  <div style={{ width: 1, background: 'var(--border-subtle)' }} />
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: '1.2rem', fontWeight: 700, color: (stats?.transfers?.late ?? 0) > 0 ? 'var(--color-error)' : 'var(--text-primary)' }}>
                      {stats?.transfers?.late ?? 0}
                    </div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Late</div>
                  </div>
                  <div style={{ width: 1, background: 'var(--border-subtle)' }} />
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: '1.2rem', fontWeight: 700 }}>
                      {stats?.transfers?.total_operations ?? 0}
                    </div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Total</div>
                  </div>
                </div>
              </div>
            )}

            {/* Adjustments Card */}
            {showAdjustments && (
              <div
                className="card hover-card"
                style={{ cursor: 'pointer', transition: 'all 0.2s', display: 'flex', flexDirection: 'column' }}
                onClick={() => navigate('/adjustments')}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-3)', marginBottom: 'var(--space-6)' }}>
                  <div style={{
                    width: 40, height: 40, borderRadius: 'var(--radius-md)',
                    background: 'hsla(217,91%,60%,0.15)', color: 'var(--color-info)',
                    display: 'flex', alignItems: 'center', justifyContent: 'center'
                  }}>
                    <SlidersHorizontal size={20} />
                  </div>
                  <h2 style={{ fontSize: '1.25rem', fontWeight: 600 }}>Adjustments</h2>
                </div>
                <div style={{ flex: 1 }}>
                  <div style={{ fontSize: '3rem', fontWeight: 800, lineHeight: 1, marginBottom: 'var(--space-2)' }}>
                    {adjustmentMetric.count}
                  </div>
                  <div style={{ fontSize: '1.125rem', color: 'var(--text-secondary)', fontWeight: 500, marginBottom: 'var(--space-6)' }}>
                    {adjustmentMetric.label}
                  </div>
                </div>
                <div style={{ display: 'flex', gap: 'var(--space-4)', borderTop: '1px solid var(--border-subtle)', paddingTop: 'var(--space-4)' }}>
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: '1.2rem', fontWeight: 700, color: 'var(--color-success)' }}>
                      {stats?.adjustments?.done ?? 0}
                    </div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Done</div>
                  </div>
                  <div style={{ width: 1, background: 'var(--border-subtle)' }} />
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: '1.2rem', fontWeight: 700, color: (stats?.adjustments?.late ?? 0) > 0 ? 'var(--color-error)' : 'var(--text-primary)' }}>
                      {stats?.adjustments?.late ?? 0}
                    </div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Late</div>
                  </div>
                  <div style={{ width: 1, background: 'var(--border-subtle)' }} />
                  <div style={{ flex: 1 }}>
                    <div style={{ fontSize: '1.2rem', fontWeight: 700 }}>
                      {stats?.adjustments?.total_operations ?? 0}
                    </div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Total</div>
                  </div>
                </div>
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
}
