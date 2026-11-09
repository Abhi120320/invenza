import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { dashboardApi } from '../api/client';
import { ArrowDownToLine, ArrowUpFromLine, ArrowRightLeft, SlidersHorizontal, RefreshCw } from 'lucide-react';
import toast from 'react-hot-toast';

export default function OperationsDashboardPage() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const navigate = useNavigate();

  const fetchStats = useCallback(async (isSilent = false) => {
    if (!isSilent) setRefreshing(true);
    try {
      const { data } = await dashboardApi.kpis();
      setStats(data);
    } catch (err) {
      if (!isSilent) toast.error('Failed to load operation stats');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    fetchStats();
  }, [fetchStats]);

  // Live Auto-Refresh (WebSocket + Polling fallback)
  useEffect(() => {
    let ws = null;
    try {
      const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
      const host = window.location.hostname;
      const port = '8000';
      ws = new WebSocket(`${protocol}//${host}:${port}/ws/dashboard`);
      ws.onmessage = () => fetchStats(true);
    } catch (e) {
      // Ignore WS error
    }

    const interval = setInterval(() => {
      fetchStats(true);
    }, 4000);

    return () => {
      if (ws) ws.close();
      clearInterval(interval);
    };
  }, [fetchStats]);

  const renderBadge = (opStats, defaultText) => {
    if (!opStats) return <span className="text-muted" style={{ fontSize: '0.875rem' }}>No operations</span>;

    const toDo = opStats.to_do ?? 0;
    const done = opStats.done ?? 0;

    if (toDo > 0) {
      return (
        <span className="badge badge-waiting" style={{ fontSize: '0.875rem', padding: '6px 12px' }}>
          {toDo} {defaultText}
        </span>
      );
    }
    if (done > 0) {
      return (
        <span className="badge badge-done" style={{ fontSize: '0.875rem', padding: '6px 12px' }}>
          {done} completed
        </span>
      );
    }
    return <span className="text-muted" style={{ fontSize: '0.875rem' }}>No pending tasks</span>;
  };

  return (
    <div>
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h1 className="page-title">Operations</h1>
          <p className="page-subtitle">Manage your warehouse tasks and movements</p>
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

      <div style={{ padding: 'var(--space-8)' }}>
        {loading ? (
          <div style={{ display: 'flex', justifyContent: 'center', padding: 'var(--space-8)' }}>
            <div className="spinner" />
          </div>
        ) : (
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
            gap: 'var(--space-6)',
          }}>
            {/* Receipts Card */}
            <div 
              className="card hover-card" 
              style={{ padding: 'var(--space-6)', cursor: 'pointer', display: 'flex', flexDirection: 'column', gap: 'var(--space-4)' }}
              onClick={() => navigate('/receipts')}
            >
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-3)' }}>
                  <div style={{ background: 'hsla(168,85%,48%,0.15)', padding: 'var(--space-3)', borderRadius: 'var(--radius-md)' }}>
                    <ArrowDownToLine size={24} color="var(--brand-primary)" />
                  </div>
                  <h3 style={{ margin: 0, fontSize: '1.25rem', fontWeight: 600 }}>Receipts</h3>
                </div>
              </div>
              <div style={{ marginTop: 'var(--space-2)' }}>
                {renderBadge(stats?.receipts, 'to receive')}
              </div>
              <div style={{ marginTop: 'auto', fontSize: '0.8rem', color: 'var(--text-muted)', borderTop: '1px solid var(--border-subtle)', paddingTop: 'var(--space-3)' }}>
                {stats?.receipts?.total_operations ?? 0} total receipts
              </div>
            </div>

            {/* Delivery Card */}
            <div 
              className="card hover-card" 
              style={{ padding: 'var(--space-6)', cursor: 'pointer', display: 'flex', flexDirection: 'column', gap: 'var(--space-4)' }}
              onClick={() => navigate('/deliveries')}
            >
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-3)' }}>
                  <div style={{ background: 'hsla(152,70%,48%,0.15)', padding: 'var(--space-3)', borderRadius: 'var(--radius-md)' }}>
                    <ArrowUpFromLine size={24} color="var(--color-success)" />
                  </div>
                  <h3 style={{ margin: 0, fontSize: '1.25rem', fontWeight: 600 }}>Deliveries</h3>
                </div>
              </div>
              <div style={{ marginTop: 'var(--space-2)' }}>
                {renderBadge(stats?.deliveries, 'to deliver')}
              </div>
              <div style={{ marginTop: 'auto', fontSize: '0.8rem', color: 'var(--text-muted)', borderTop: '1px solid var(--border-subtle)', paddingTop: 'var(--space-3)' }}>
                {stats?.deliveries?.total_operations ?? 0} total deliveries
              </div>
            </div>

            {/* Internal Transfers Card */}
            <div 
              className="card hover-card" 
              style={{ padding: 'var(--space-6)', cursor: 'pointer', display: 'flex', flexDirection: 'column', gap: 'var(--space-4)' }}
              onClick={() => navigate('/transfers')}
            >
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-3)' }}>
                  <div style={{ background: 'hsla(45,100%,51%,0.15)', padding: 'var(--space-3)', borderRadius: 'var(--radius-md)' }}>
                    <ArrowRightLeft size={24} color="var(--color-warning)" />
                  </div>
                  <h3 style={{ margin: 0, fontSize: '1.25rem', fontWeight: 600 }}>Internal Transfers</h3>
                </div>
              </div>
              <div style={{ marginTop: 'var(--space-2)' }}>
                {renderBadge(stats?.transfers, 'scheduled')}
              </div>
              <div style={{ marginTop: 'auto', fontSize: '0.8rem', color: 'var(--text-muted)', borderTop: '1px solid var(--border-subtle)', paddingTop: 'var(--space-3)' }}>
                {stats?.transfers?.total_operations ?? 0} total transfers
              </div>
            </div>

            {/* Inventory Adjustment Card */}
            <div 
              className="card hover-card" 
              style={{ padding: 'var(--space-6)', cursor: 'pointer', display: 'flex', flexDirection: 'column', gap: 'var(--space-4)' }}
              onClick={() => navigate('/adjustments')}
            >
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-3)' }}>
                  <div style={{ background: 'hsla(217,91%,60%,0.15)', padding: 'var(--space-3)', borderRadius: 'var(--radius-md)' }}>
                    <SlidersHorizontal size={24} color="var(--color-info)" />
                  </div>
                  <h3 style={{ margin: 0, fontSize: '1.25rem', fontWeight: 600 }}>Adjustments</h3>
                </div>
              </div>
              <div style={{ marginTop: 'var(--space-2)' }}>
                {renderBadge(stats?.adjustments, 'pending')}
              </div>
              <div style={{ marginTop: 'auto', fontSize: '0.8rem', color: 'var(--text-muted)', borderTop: '1px solid var(--border-subtle)', paddingTop: 'var(--space-3)' }}>
                {stats?.adjustments?.total_operations ?? 0} total adjustments
              </div>
            </div>

          </div>
        )}
      </div>
    </div>
  );
}
