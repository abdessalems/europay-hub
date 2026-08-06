import { useQuery } from "@tanstack/react-query";
import { TrendingUp, ShoppingCart, CreditCard, Clock } from "lucide-react";
import { http, unwrap } from "@/lib/api";
import type { Order, Page, Payment } from "@/lib/types";
import { formatMoney, formatDate } from "@/lib/format";
import { Card, CardContent } from "@/components/ui/card";
import { Table, THead, TBody, TR, TH, TD } from "@/components/ui/table";
import { StatusBadge } from "@/components/ui/badge";
import { PageHeader } from "@/components/PageHeader";

export function DashboardPage() {
  const { data: orders } = useQuery({
    queryKey: ["orders", "all"],
    queryFn: () => unwrap<Page<Order>>(http.get("/api/orders?page=0&size=100")),
  });
  const { data: payments } = useQuery({
    queryKey: ["payments", "all"],
    queryFn: () => unwrap<Page<Payment>>(http.get("/api/payments?page=0&size=100")),
  });

  const paid = (payments?.content ?? []).filter((p) => ["SUCCESS", "SETTLED"].includes(p.status));
  const revenue = paid.reduce((sum, p) => sum + p.amount, 0);
  const pending = (payments?.content ?? []).filter((p) => p.status === "PENDING").length;

  const stats = [
    { label: "Revenue (captured)", value: formatMoney(revenue), icon: TrendingUp, tint: "text-success" },
    { label: "Orders", value: String(orders?.totalElements ?? 0), icon: ShoppingCart, tint: "text-primary" },
    { label: "Payments", value: String(payments?.totalElements ?? 0), icon: CreditCard, tint: "text-primary" },
    { label: "Pending", value: String(pending), icon: Clock, tint: "text-warning" },
  ];

  return (
    <>
      <PageHeader title="Dashboard" description="Your payment activity at a glance." />

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {stats.map((s) => (
          <Card key={s.label}>
            <CardContent className="flex items-center justify-between p-5">
              <div>
                <p className="text-sm text-muted-foreground">{s.label}</p>
                <p className="mt-1 text-2xl font-bold tracking-tight">{s.value}</p>
              </div>
              <div className="rounded-xl bg-accent p-3">
                <s.icon className={`size-5 ${s.tint}`} />
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      <Card className="mt-6">
        <div className="border-b border-border px-6 py-4">
          <h2 className="font-semibold">Recent payments</h2>
        </div>
        <Table>
          <THead>
            <TR>
              <TH>Reference</TH><TH>Method</TH><TH>Amount</TH><TH>Status</TH><TH>Created</TH>
            </TR>
          </THead>
          <TBody>
            {(payments?.content ?? []).slice(0, 8).map((p) => (
              <TR key={p.id}>
                <TD className="font-mono text-xs">{p.providerReference ?? p.id.slice(0, 8)}</TD>
                <TD>{p.paymentMethod}</TD>
                <TD className="font-medium">{formatMoney(p.amount, p.currency)}</TD>
                <TD><StatusBadge status={p.status} /></TD>
                <TD className="text-muted-foreground">{formatDate(p.createdAt)}</TD>
              </TR>
            ))}
            {(payments?.content ?? []).length === 0 && (
              <TR><TD colSpan={5} className="py-10 text-center text-muted-foreground">No payments yet.</TD></TR>
            )}
          </TBody>
        </Table>
      </Card>
    </>
  );
}
