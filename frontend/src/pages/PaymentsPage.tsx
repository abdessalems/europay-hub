import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { http, unwrap } from "@/lib/api";
import type { Page, Payment } from "@/lib/types";
import { formatMoney, formatDate } from "@/lib/format";
import { Card } from "@/components/ui/card";
import { Table, THead, TBody, TR, TH, TD } from "@/components/ui/table";
import { StatusBadge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { PageHeader } from "@/components/PageHeader";

export function PaymentsPage() {
  const qc = useQueryClient();

  const { data } = useQuery({
    queryKey: ["payments", "list"],
    queryFn: () => unwrap<Page<Payment>>(http.get("/api/payments?page=0&size=50")),
  });

  const action = useMutation({
    mutationFn: (vars: { id: string; verb: "approve" | "cancel" | "refund" | "retry" }) =>
      unwrap(http.post(`/api/payments/${vars.id}/${vars.verb}`)),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["payments"] });
      qc.invalidateQueries({ queryKey: ["orders"] });
    },
  });

  const rowActions = (p: Payment) => {
    const btns: { verb: "approve" | "cancel" | "refund" | "retry"; label: string; variant?: "outline" | "destructive" | "default" }[] = [];
    if (["PENDING", "AUTHORIZED"].includes(p.status)) {
      btns.push({ verb: "approve", label: "Approve" });
      btns.push({ verb: "cancel", label: "Cancel", variant: "outline" });
    }
    if (["SUCCESS", "SETTLED"].includes(p.status)) btns.push({ verb: "refund", label: "Refund", variant: "destructive" });
    if (p.status === "FAILED") btns.push({ verb: "retry", label: "Retry", variant: "outline" });
    return btns;
  };

  return (
    <>
      <PageHeader title="Payments" description="Track and act on every payment." />
      <Card>
        <Table>
          <THead>
            <TR>
              <TH>Reference</TH><TH>Method</TH><TH>Amount</TH><TH>Status</TH><TH>Created</TH><TH className="text-right">Actions</TH>
            </TR>
          </THead>
          <TBody>
            {(data?.content ?? []).map((p) => (
              <TR key={p.id}>
                <TD className="font-mono text-xs">{p.providerReference ?? p.id.slice(0, 8)}</TD>
                <TD>{p.paymentMethod}</TD>
                <TD className="font-medium">{formatMoney(p.amount, p.currency)}</TD>
                <TD><StatusBadge status={p.status} /></TD>
                <TD className="text-muted-foreground">{formatDate(p.createdAt)}</TD>
                <TD className="text-right">
                  <div className="inline-flex gap-1">
                    {rowActions(p).map((a) => (
                      <Button key={a.verb} size="sm" variant={a.variant ?? "default"}
                        disabled={action.isPending}
                        onClick={() => action.mutate({ id: p.id, verb: a.verb })}>
                        {a.label}
                      </Button>
                    ))}
                    {rowActions(p).length === 0 && <span className="text-xs text-muted-foreground">—</span>}
                  </div>
                </TD>
              </TR>
            ))}
            {(data?.content ?? []).length === 0 && (
              <TR><TD colSpan={6} className="py-10 text-center text-muted-foreground">No payments yet.</TD></TR>
            )}
          </TBody>
        </Table>
      </Card>
    </>
  );
}
