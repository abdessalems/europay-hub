import { useQuery } from "@tanstack/react-query";
import { TrendingUp, ShoppingCart, CreditCard, CheckCircle2 } from "lucide-react";
import { http, unwrap } from "@/lib/api";
import type { Order, Page, Payment } from "@/lib/types";
import { formatMoney, formatDate } from "@/lib/format";
import { revenueSeries, methodBreakdown } from "@/lib/analytics";
import { Card, CardContent } from "@/components/ui/card";
import { Table, THead, TBody, TR, TH, TD } from "@/components/ui/table";
import { StatusBadge } from "@/components/ui/badge";
import { PageHeader } from "@/components/PageHeader";
import { StatCard } from "@/components/StatCard";
import { MethodLogo } from "@/components/MethodLogo";
import { RevenueAreaChart } from "@/components/charts/RevenueAreaChart";
import { MethodDonut } from "@/components/charts/MethodDonut";

export function DashboardPage() {
  const { data: orders } = useQuery({
    queryKey: ["orders", "all"],
    queryFn: () => unwrap<Page<Order>>(http.get("/api/orders?page=0&size=100")),
  });
  const { data: payments } = useQuery({
    queryKey: ["payments", "all"],
    queryFn: () => unwrap<Page<Payment>>(http.get("/api/payments?page=0&size=100")),
  });

  const rows = payments?.content ?? [];
  const captured = rows.filter((p) => ["SUCCESS", "SETTLED"].includes(p.status));
  const revenue = captured.reduce((s, p) => s + p.amount, 0);
  const pending = rows.filter((p) => p.status === "PENDING").length;
  const paidOrders = (orders?.content ?? []).filter((o) => o.status === "PAID").length;
  const successRate = rows.length ? Math.round((captured.length / rows.length) * 100) : 0;

  return (
    <>
      <PageHeader title="Dashboard" description="Your payment activity at a glance." />

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard label="Revenue (captured)" value={formatMoney(revenue)} tint="text-success"
          icon={TrendingUp} hint={`${captured.length} captured payment${captured.length === 1 ? "" : "s"}`} />
        <StatCard label="Orders" value={String(orders?.totalElements ?? 0)}
          icon={ShoppingCart} hint={`${paidOrders} paid`} />
        <StatCard label="Payments" value={String(payments?.totalElements ?? 0)}
          icon={CreditCard} hint={`${pending} pending`} />
        <StatCard label="Success rate" value={`${successRate}%`} tint="text-success"
          icon={CheckCircle2} hint="captured vs total" />
      </div>

      <div className="mt-4 grid gap-4 lg:grid-cols-3">
        <Card className="lg:col-span-2">
          <div className="flex items-center justify-between border-b border-border px-6 py-4">
            <div>
              <h2 className="font-semibold">Revenue</h2>
              <p className="text-xs text-muted-foreground">Captured, last 14 days</p>
            </div>
            <span className="text-lg font-bold">{formatMoney(revenue)}</span>
          </div>
          <CardContent className="p-4">
            <RevenueAreaChart data={revenueSeries(rows, 14)} />
          </CardContent>
        </Card>

        <Card>
          <div className="border-b border-border px-6 py-4">
            <h2 className="font-semibold">Payment methods</h2>
            <p className="text-xs text-muted-foreground">Share by count</p>
          </div>
          <CardContent className="flex items-center justify-center p-6">
            <MethodDonut data={methodBreakdown(rows)} />
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
            {rows.slice(0, 8).map((p) => (
              <TR key={p.id}>
                <TD className="font-mono text-xs">{p.providerReference ?? p.id.slice(0, 8)}</TD>
                <TD><MethodLogo method={p.paymentMethod} /></TD>
                <TD className="font-medium">{formatMoney(p.amount, p.currency)}</TD>
                <TD><StatusBadge status={p.status} /></TD>
                <TD className="text-muted-foreground">{formatDate(p.createdAt)}</TD>
              </TR>
            ))}
            {rows.length === 0 && (
              <TR><TD colSpan={5} className="py-10 text-center text-muted-foreground">No payments yet.</TD></TR>
            )}
          </TBody>
        </Table>
      </Card>
    </>
  );
}
