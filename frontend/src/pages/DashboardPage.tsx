import { useQuery } from "@tanstack/react-query";
import { TrendingUp, ShoppingCart, CreditCard, CheckCircle2 } from "lucide-react";
import { http, unwrap } from "@/lib/api";
import type { DashboardMetrics, Page, Payment } from "@/lib/types";
import { formatMoney, formatDate } from "@/lib/format";
import { Card, CardContent } from "@/components/ui/card";
import { Table, THead, TBody, TR, TH, TD } from "@/components/ui/table";
import { StatusBadge } from "@/components/ui/badge";
import { PageHeader } from "@/components/PageHeader";
import { StatCard } from "@/components/StatCard";
import { MethodLogo } from "@/components/MethodLogo";
import { RevenueAreaChart } from "@/components/charts/RevenueAreaChart";
import { MethodDonut } from "@/components/charts/MethodDonut";

function dayLabel(date: string) {
  return new Date(date).toLocaleDateString("en-GB", { day: "2-digit", month: "short" });
}

export function DashboardPage() {
  const { data: metrics } = useQuery({
    queryKey: ["dashboard"],
    queryFn: () => unwrap<DashboardMetrics>(http.get("/api/dashboard")),
  });
  const { data: recent } = useQuery({
    queryKey: ["payments", "recent"],
    queryFn: () => unwrap<Page<Payment>>(http.get("/api/payments?page=0&size=8")),
  });

  const revenueSeries = (metrics?.revenueByDay ?? []).map((d) => ({ label: dayLabel(d.date), value: d.amount }));
  const methodSeries = (metrics?.paymentsByMethod ?? []).map((m) => ({ name: m.key, value: m.count }));

  return (
    <>
      <PageHeader title="Dashboard" description="Your payment activity at a glance." />

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard label="Revenue (captured)" value={formatMoney(metrics?.revenue ?? 0)} tint="text-success"
          icon={TrendingUp} hint="SUCCESS + SETTLED" />
        <StatCard label="Orders" value={String(metrics?.orderCount ?? 0)} icon={ShoppingCart} hint="all time" />
        <StatCard label="Payments" value={String(metrics?.paymentCount ?? 0)} icon={CreditCard}
          hint={`${metrics?.pendingCount ?? 0} pending`} />
        <StatCard label="Success rate" value={`${metrics?.successRate ?? 0}%`} tint="text-success"
          icon={CheckCircle2} hint="captured vs total" />
      </div>

      <div className="mt-4 grid gap-4 lg:grid-cols-3">
        <Card className="lg:col-span-2">
          <div className="flex items-center justify-between border-b border-border px-6 py-4">
            <div>
              <h2 className="font-semibold">Revenue</h2>
              <p className="text-xs text-muted-foreground">Captured, last 14 days</p>
            </div>
            <span className="text-lg font-bold">{formatMoney(metrics?.revenue ?? 0)}</span>
          </div>
          <CardContent className="p-4">
            <RevenueAreaChart data={revenueSeries} />
          </CardContent>
        </Card>

        <Card>
          <div className="border-b border-border px-6 py-4">
            <h2 className="font-semibold">Payment methods</h2>
            <p className="text-xs text-muted-foreground">Share by count</p>
          </div>
          <CardContent className="flex items-center justify-center p-6">
            <MethodDonut data={methodSeries} />
          </CardContent>
        </Card>
      </div>

      <Card className="mt-4">
        <div className="border-b border-border px-6 py-4">
          <h2 className="font-semibold">Recent payments</h2>
        </div>
        <Table>
          <THead>
            <TR><TH>Reference</TH><TH>Method</TH><TH>Amount</TH><TH>Status</TH><TH>Created</TH></TR>
          </THead>
          <TBody>
            {(recent?.content ?? []).map((p) => (
              <TR key={p.id}>
                <TD className="font-mono text-xs">{p.providerReference ?? p.id.slice(0, 8)}</TD>
                <TD><MethodLogo method={p.paymentMethod} /></TD>
                <TD className="font-medium">{formatMoney(p.amount, p.currency)}</TD>
                <TD><StatusBadge status={p.status} /></TD>
                <TD className="text-muted-foreground">{formatDate(p.createdAt)}</TD>
              </TR>
            ))}
            {(recent?.content ?? []).length === 0 && (
              <TR><TD colSpan={5} className="py-10 text-center text-muted-foreground">No payments yet.</TD></TR>
            )}
          </TBody>
        </Table>
      </Card>
    </>
  );
}
