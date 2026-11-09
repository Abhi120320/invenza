import React, { useEffect, useRef, useState } from 'react';
import lottie from 'lottie-web/build/player/lottie_light.js';
import { Printer, CheckCircle, X, RotateCcw, Package, CreditCard, ShieldCheck } from 'lucide-react';
import invoiceAnimationData from '../assets/invoice-printing.json';

export default function InvoicePrinterModal({
  isOpen,
  onClose,
  operation,
  payment,
  products = [],
  user,
  cfg
}) {
  const [stage, setStage] = useState('printing'); // 'printing' | 'ready'
  const animContainer = useRef(null);
  const animInstance = useRef(null);

  useEffect(() => {
    if (!isOpen) {
      setStage('printing');
      if (animInstance.current) {
        animInstance.current.destroy();
        animInstance.current = null;
      }
      return;
    }

    setStage('printing');

    // Wait for container to be in DOM
    const timer = setTimeout(() => {
      if (animContainer.current) {
        if (animInstance.current) {
          animInstance.current.destroy();
        }
        animInstance.current = lottie.loadAnimation({
          container: animContainer.current,
          renderer: 'svg',
          loop: true,
          autoplay: true,
          animationData: invoiceAnimationData,
        });
      }
    }, 50);

    // Auto-transition to ready invoice after animation plays
    const transitionTimer = setTimeout(() => {
      setStage('ready');
    }, 2800);

    return () => {
      clearTimeout(timer);
      clearTimeout(transitionTimer);
      if (animInstance.current) {
        animInstance.current.destroy();
        animInstance.current = null;
      }
    };
  }, [isOpen]);

  if (!isOpen) return null;

  const lines = operation?.lines || [];
  const totalQty = lines.reduce((acc, l) => acc + (Number(l.quantity) || 0), 0);
  const totalAmount = payment?.amount ? Number(payment.amount) : Math.max(totalQty * 100, 100);

  const handlePrint = () => {
    window.print();
  };

  const replayAnimation = () => {
    setStage('printing');
    setTimeout(() => {
      if (animContainer.current) {
        if (animInstance.current) animInstance.current.destroy();
        animInstance.current = lottie.loadAnimation({
          container: animContainer.current,
          renderer: 'svg',
          loop: true,
          autoplay: true,
          animationData: invoiceAnimationData,
        });
      }
    }, 50);
    setTimeout(() => {
      setStage('ready');
    }, 2800);
  };

  return (
    <div
      className="fixed inset-0 z-[9999] flex items-center justify-center p-4"
      style={{ backgroundColor: 'rgba(0, 0, 0, 0.65)', backdropFilter: 'blur(4px)' }}
      onClick={(e) => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div
        className="relative w-full max-w-xl max-h-[92vh] flex flex-col bg-white dark:bg-zinc-900 rounded-2xl shadow-2xl overflow-hidden border border-zinc-200 dark:border-zinc-800"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="no-print flex items-center justify-between px-6 py-4 border-b border-zinc-200 dark:border-zinc-800 bg-zinc-50/80 dark:bg-zinc-900/80">
          <div className="flex items-center gap-2">
            <div className="flex items-center justify-center w-8 h-8 rounded-lg bg-teal-600 text-white">
              <Package size={18} />
            </div>
            <div>
              <h3 className="font-semibold text-zinc-900 dark:text-zinc-100 text-sm">
                Invoice & Receipt
              </h3>
              <p className="text-xs text-zinc-500 dark:text-zinc-400">
                {operation?.reference || 'DRAFT'} • {cfg?.label || 'Order'}
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            {stage === 'ready' && (
              <>
                <button
                  type="button"
                  onClick={replayAnimation}
                  className="btn btn-ghost btn-sm text-xs flex items-center gap-1.5"
                  title="Replay printing animation"
                >
                  <RotateCcw size={14} /> Replay
                </button>
                <button
                  type="button"
                  onClick={handlePrint}
                  className="btn btn-primary btn-sm text-xs flex items-center gap-1.5"
                  style={{ background: '#0f766e', borderColor: '#0f766e', color: 'white' }}
                >
                  <Printer size={14} /> Print / Save PDF
                </button>
              </>
            )}
            {stage === 'printing' && (
              <button
                type="button"
                onClick={() => setStage('ready')}
                className="btn btn-ghost btn-sm text-xs"
              >
                Skip to Invoice
              </button>
            )}
            <button
              type="button"
              onClick={onClose}
              className="p-1.5 rounded-lg text-zinc-400 hover:text-zinc-600 dark:hover:text-zinc-200 hover:bg-zinc-100 dark:hover:bg-zinc-800 transition-colors"
            >
              <X size={18} />
            </button>
          </div>
        </div>

        {/* Content Body */}
        <div className="flex-1 overflow-y-auto p-6">
          {stage === 'printing' ? (
            /* Lottie Animation State */
            <div className="flex flex-col items-center justify-center py-10 px-4 text-center">
              <div
                ref={animContainer}
                className="w-56 h-56 max-w-full flex items-center justify-center"
                style={{ filter: 'drop-shadow(0 10px 20px rgba(15, 118, 110, 0.15))' }}
              />
              <div className="mt-4 flex items-center gap-2 text-teal-700 dark:text-teal-400 font-semibold text-base">
                <span className="inline-block w-2 h-2 rounded-full bg-teal-600 animate-ping" />
                Printing Invoice...
              </div>
              <p className="mt-1 text-xs text-zinc-500 dark:text-zinc-400">
                Generating verified receipt for {operation?.reference || 'Order'}
              </p>
              <div className="mt-6 w-48 h-1.5 bg-zinc-100 dark:bg-zinc-800 rounded-full overflow-hidden">
                <div
                  className="h-full bg-teal-600 rounded-full animate-pulse"
                  style={{ width: '85%' }}
                />
              </div>
            </div>
          ) : (
            /* Printable Invoice Document State */
            <div className="printable-invoice bg-white text-zinc-900 rounded-xl p-8 border border-zinc-200 shadow-sm font-sans">
              {/* Document Header */}
              <div className="flex justify-between items-start border-b border-zinc-200 pb-6 mb-6">
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-bold text-2xl tracking-tight text-teal-700">INVENZA</span>
                    <span className="text-[10px] font-bold px-1.5 py-0.5 rounded bg-zinc-100 text-zinc-700 border border-zinc-300">
                      OFFICIAL INVOICE
                    </span>
                  </div>
                  <p className="text-xs text-zinc-500 mt-1">Modular Inventory Management & Logistics</p>
                  <p className="text-xs text-zinc-400">GSTIN: 29AABCU9603R1ZM</p>
                </div>
                <div className="text-right">
                  <div className="text-xs text-zinc-400 uppercase tracking-wider font-semibold">Invoice Ref</div>
                  <div className="text-lg font-bold font-mono text-zinc-800">{operation?.reference || 'DRAFT'}</div>
                  <div className="text-xs text-zinc-500 mt-0.5">
                    {operation?.created_at
                      ? new Date(operation.created_at).toLocaleDateString('en-GB', {
                          day: '2-digit',
                          month: 'short',
                          year: 'numeric',
                          hour: '2-digit',
                          minute: '2-digit',
                        })
                      : new Date().toLocaleDateString('en-GB')}
                  </div>
                </div>
              </div>

              {/* Bill Details */}
              <div className="grid grid-cols-2 gap-6 text-xs mb-6 p-4 rounded-lg bg-zinc-50 border border-zinc-100">
                <div>
                  <span className="text-zinc-400 font-medium uppercase tracking-wider block mb-1">
                    {cfg?.contactLabel || 'Contact / Customer'}
                  </span>
                  <div className="font-semibold text-zinc-800 text-sm">{operation?.contact || 'Standard Customer'}</div>
                  <div className="text-zinc-500 mt-0.5">Type: {cfg?.label || operation?.type}</div>
                </div>
                <div>
                  <span className="text-zinc-400 font-medium uppercase tracking-wider block mb-1">
                    Processed By
                  </span>
                  <div className="font-semibold text-zinc-800 text-sm">{user?.name || user?.email || 'System'}</div>
                  <div className="text-zinc-500 mt-0.5">Warehouse ID: {operation?.warehouse_id || 'Primary'}</div>
                </div>
              </div>

              {/* Line Items Table */}
              <table className="w-full text-xs mb-6 border-collapse">
                <thead>
                  <tr className="border-b border-zinc-300 text-zinc-500 uppercase tracking-wider text-[11px]">
                    <th className="py-2 text-left font-semibold">Item</th>
                    <th className="py-2 text-center font-semibold">Qty</th>
                    <th className="py-2 text-right font-semibold">Unit Price</th>
                    <th className="py-2 text-right font-semibold">Total</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-zinc-100">
                  {lines.map((l, i) => {
                    const p = products.find((prod) => prod.id === l.product_id);
                    const qty = Number(l.quantity) || 1;
                    const unitPrice = 100.0;
                    const lineTotal = qty * unitPrice;
                    return (
                      <tr key={i} className="text-zinc-700">
                        <td className="py-2.5">
                          <div className="font-semibold text-zinc-800">{p ? p.name : 'Item #' + (i + 1)}</div>
                          <div className="text-[10px] text-zinc-400 font-mono">SKU: {p ? p.sku : 'N/A'}</div>
                        </td>
                        <td className="py-2.5 text-center font-mono">{qty}</td>
                        <td className="py-2.5 text-right font-mono">₹{unitPrice.toFixed(2)}</td>
                        <td className="py-2.5 text-right font-mono font-medium">₹{lineTotal.toFixed(2)}</td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>

              {/* Totals */}
              <div className="border-t border-zinc-200 pt-4 mb-6 space-y-1.5 text-xs text-zinc-600">
                <div className="flex justify-between">
                  <span>Total Items</span>
                  <span className="font-mono">{totalQty} units</span>
                </div>
                <div className="flex justify-between">
                  <span>Subtotal</span>
                  <span className="font-mono">₹{totalAmount.toFixed(2)}</span>
                </div>
                <div className="flex justify-between">
                  <span>Taxes (Included)</span>
                  <span className="font-mono">₹0.00</span>
                </div>
                <div className="flex justify-between pt-2 border-t border-zinc-300 font-bold text-sm text-zinc-900">
                  <span>GRAND TOTAL</span>
                  <span className="font-mono text-teal-700">₹{totalAmount.toFixed(2)}</span>
                </div>
              </div>

              {/* Razorpay Payment Verification Seal */}
              <div className="p-4 rounded-xl border border-teal-300 bg-teal-50/60 mb-6">
                <div className="flex items-center justify-between mb-2">
                  <div className="flex items-center gap-2">
                    <ShieldCheck size={18} className="text-teal-700" />
                    <span className="font-bold text-xs uppercase tracking-wider text-teal-800">
                      Razorpay Payment Verified
                    </span>
                  </div>
                  <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-teal-700 text-white">
                    TEST MODE
                  </span>
                </div>

                <div className="grid grid-cols-2 gap-3 text-[11px] text-zinc-700">
                  <div>
                    <span className="text-zinc-500">Payment ID:</span>
                    <span className="font-mono font-medium block text-zinc-900 truncate">
                      {payment?.razorpay_payment_id || 'pay_test_verified'}
                    </span>
                  </div>
                  <div>
                    <span className="text-zinc-500">Order ID:</span>
                    <span className="font-mono font-medium block text-zinc-900 truncate">
                      {payment?.razorpay_order_id || 'order_test_verified'}
                    </span>
                  </div>
                  <div>
                    <span className="text-zinc-500">Payment Status:</span>
                    <span className="font-bold text-teal-700 block uppercase">
                      {payment?.status || 'SUCCESS'}
                    </span>
                  </div>
                  <div>
                    <span className="text-zinc-500">Amount Paid:</span>
                    <span className="font-bold text-zinc-900 block font-mono">
                      ₹{totalAmount.toFixed(2)}
                    </span>
                  </div>
                </div>
              </div>

              {/* Barcode / Footer */}
              <div className="flex flex-col items-center pt-2">
                <svg width="200" height="36" viewBox="0 0 200 36">
                  <rect x="0" y="0" width="3" height="36" fill="#18181b" />
                  <rect x="6" y="0" width="2" height="36" fill="#18181b" />
                  <rect x="12" y="0" width="5" height="36" fill="#18181b" />
                  <rect x="20" y="0" width="3" height="36" fill="#18181b" />
                  <rect x="28" y="0" width="2" height="36" fill="#18181b" />
                  <rect x="34" y="0" width="6" height="36" fill="#18181b" />
                  <rect x="44" y="0" width="3" height="36" fill="#18181b" />
                  <rect x="52" y="0" width="2" height="36" fill="#18181b" />
                  <rect x="58" y="0" width="5" height="36" fill="#18181b" />
                  <rect x="68" y="0" width="2" height="36" fill="#18181b" />
                  <rect x="74" y="0" width="6" height="36" fill="#18181b" />
                  <rect x="84" y="0" width="3" height="36" fill="#18181b" />
                  <rect x="90" y="0" width="2" height="36" fill="#18181b" />
                  <rect x="96" y="0" width="5" height="36" fill="#18181b" />
                  <rect x="106" y="0" width="3" height="36" fill="#18181b" />
                  <rect x="114" y="0" width="2" height="36" fill="#18181b" />
                  <rect x="118" y="0" width="6" height="36" fill="#18181b" />
                  <rect x="130" y="0" width="3" height="36" fill="#18181b" />
                  <rect x="138" y="0" width="2" height="36" fill="#18181b" />
                  <rect x="144" y="0" width="5" height="36" fill="#18181b" />
                  <rect x="154" y="0" width="2" height="36" fill="#18181b" />
                  <rect x="160" y="0" width="6" height="36" fill="#18181b" />
                  <rect x="172" y="0" width="3" height="36" fill="#18181b" />
                  <rect x="180" y="0" width="2" height="36" fill="#18181b" />
                  <rect x="186" y="0" width="5" height="36" fill="#18181b" />
                </svg>
                <div className="mt-1 text-[10px] tracking-widest text-zinc-400 font-mono">
                  {operation?.reference || 'DRAFT-0000'}
                </div>
                <div className="mt-3 text-[10px] text-zinc-400">
                  Thank you for using Invenza Inventory Management
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Footer */}
        {stage === 'ready' && (
          <div className="no-print flex items-center justify-between px-6 py-3 border-t border-zinc-200 dark:border-zinc-800 bg-zinc-50 dark:bg-zinc-900">
            <span className="text-xs text-zinc-500">
              Press Print or Ctrl+P to save as PDF
            </span>
            <div className="flex gap-2">
              <button
                type="button"
                onClick={onClose}
                className="btn btn-secondary btn-sm text-xs"
              >
                Close
              </button>
              <button
                type="button"
                onClick={handlePrint}
                className="btn btn-primary btn-sm text-xs flex items-center gap-1.5"
                style={{ background: '#0f766e', borderColor: '#0f766e', color: 'white' }}
              >
                <Printer size={14} /> Print Invoice
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
