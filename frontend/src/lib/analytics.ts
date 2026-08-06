import type { Payment } from "./types";

const CAPTURED = ["SUCCESS", "SETTLED"];

function dayKey(iso: string) {
  return iso.slice(0, 10); // yyyy-mm-dd
}

function label(d: Date) {
  return d.toLocaleDateString("en-GB", { day: "2-digit", month: "short" });
}

/** Captured revenue per day for the last `days` days (zero-filled). */
export function revenueSeries(payments: Payment[], days = 14) {
  const totals = new Map<string, number>();
  for (const p of payments) {
    if (CAPTURED.includes(p.status)) {
      const k = dayKey(p.createdAt);
      totals.set(k, (totals.get(k) ?? 0) + p.amount);
    }
  }
  const out: { label: string; value: number }[] = [];
  const today = new Date();
  for (let i = days - 1; i >= 0; i--) {
    const d = new Date(today);
    d.setDate(today.getDate() - i);
    const k = d.toISOString().slice(0, 10);
    out.push({ label: label(d), value: Math.round((totals.get(k) ?? 0) * 100) / 100 });
  }
  return out;
}

/** Count of payments per method. */
export function methodBreakdown(payments: Payment[]) {
  const counts = new Map<string, number>();
  for (const p of payments) counts.set(p.paymentMethod, (counts.get(p.paymentMethod) ?? 0) + 1);
  return [...counts.entries()].map(([name, value]) => ({ name, value }));
}

/** Count of payments per status. */
export function statusBreakdown(payments: Payment[]) {
  const counts = new Map<string, number>();
  for (const p of payments) counts.set(p.status, (counts.get(p.status) ?? 0) + 1);
  return [...counts.entries()].map(([name, value]) => ({ name, value }));
}

export const METHOD_COLORS: Record<string, string> = {
  VISA: "hsl(347 77% 52%)",
  BANCONTACT: "hsl(210 90% 56%)",
  WERO: "hsl(38 92% 55%)",
};
