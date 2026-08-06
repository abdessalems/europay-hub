import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Plus } from "lucide-react";
import { http, unwrap } from "@/lib/api";
import type { Order, Page, PaymentMethod } from "@/lib/types";
import { formatMoney, formatDate } from "@/lib/format";
import { Card, CardContent } from "@/components/ui/card";
import { Table, THead, TBody, TR, TH, TD } from "@/components/ui/table";
import { StatusBadge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { PageHeader } from "@/components/PageHeader";
import { MethodLogo } from "@/components/MethodLogo";

const METHODS: PaymentMethod[] = ["WERO", "BANCONTACT", "VISA"];

export function OrdersPage() {
  const qc = useQueryClient();
  const [open, setOpen] = useState(false);
  const [email, setEmail] = useState("");
  const [fullName, setFullName] = useState("");
  const [amount, setAmount] = useState("");
  const [error, setError] = useState<string | null>(null);

  const { data } = useQuery({
    queryKey: ["orders", "list"],
    queryFn: () => unwrap<Page<Order>>(http.get("/api/orders?page=0&size=50")),
  });

  const createOrder = useMutation({
    mutationFn: () =>
      unwrap<Order>(http.post("/api/orders", { customer: { email, fullName }, amount })),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["orders"] });
      setOpen(false); setEmail(""); setFullName(""); setAmount(""); setError(null);
    },
    onError: (e) => setError((e as Error).message),
  });

  const pay = useMutation({
    mutationFn: (vars: { orderId: string; paymentMethod: PaymentMethod }) =>
      unwrap(http.post("/api/payments", vars)),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["orders"] });
      qc.invalidateQueries({ queryKey: ["payments"] });
    },
  });

  return (
    <>
      <PageHeader
        title="Orders"
        description="Create orders and collect payments."
        action={<Button onClick={() => setOpen((o) => !o)}><Plus /> New order</Button>}
      />

      {open && (
        <Card className="mb-6">
          <CardContent className="p-6">
            <form
              onSubmit={(e) => { e.preventDefault(); createOrder.mutate(); }}
              className="grid gap-4 sm:grid-cols-3"
            >
              <div className="space-y-1.5">
                <Label>Customer email</Label>
                <Input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="buyer@x.eu" required />
              </div>
              <div className="space-y-1.5">
                <Label>Customer name</Label>
                <Input value={fullName} onChange={(e) => setFullName(e.target.value)} placeholder="Jan Buyer" required />
              </div>
              <div className="space-y-1.5">
                <Label>Amount (EUR)</Label>
                <Input type="number" step="0.01" min="0.01" value={amount} onChange={(e) => setAmount(e.target.value)} placeholder="49.99" required />
              </div>
              {error && <p className="text-sm text-destructive sm:col-span-3">{error}</p>}
              <div className="sm:col-span-3">
                <Button type="submit" disabled={createOrder.isPending}>
                  {createOrder.isPending ? "Creating…" : "Create order"}
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      )}

      <Card>
        <Table>
          <THead>
            <TR><TH>Reference</TH><TH>Amount</TH><TH>Status</TH><TH>Created</TH><TH className="text-right">Pay</TH></TR>
          </THead>
          <TBody>
            {(data?.content ?? []).map((o) => (
              <TR key={o.id}>
                <TD className="font-mono text-xs">{o.reference}</TD>
                <TD className="font-medium">{formatMoney(o.amount, o.currency)}</TD>
                <TD><StatusBadge status={o.status} /></TD>
                <TD className="text-muted-foreground">{formatDate(o.createdAt)}</TD>
                <TD className="text-right">
                  {o.status === "CREATED" ? (
                    <div className="inline-flex items-center gap-1.5">
                      {METHODS.map((m) => (
                        <button key={m} type="button" title={`Pay with ${m}`}
                          disabled={pay.isPending}
                          className="transition hover:opacity-80 disabled:opacity-50"
                          onClick={() => pay.mutate({ orderId: o.id, paymentMethod: m })}>
                          <MethodLogo method={m} />
                        </button>
                      ))}
                    </div>
                  ) : <span className="text-xs text-muted-foreground">—</span>}
                </TD>
              </TR>
            ))}
            {(data?.content ?? []).length === 0 && (
              <TR><TD colSpan={5} className="py-10 text-center text-muted-foreground">No orders yet.</TD></TR>
            )}
          </TBody>
        </Table>
      </Card>
    </>
  );
}
