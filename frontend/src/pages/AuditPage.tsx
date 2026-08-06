import { useQuery } from "@tanstack/react-query";
import { http, unwrap } from "@/lib/api";
import type { AuditLog, Page } from "@/lib/types";
import { formatDate } from "@/lib/format";
import { Card } from "@/components/ui/card";
import { Table, THead, TBody, TR, TH, TD } from "@/components/ui/table";
import { PageHeader } from "@/components/PageHeader";

export function AuditPage() {
  const { data } = useQuery({
    queryKey: ["audit-logs"],
    queryFn: () => unwrap<Page<AuditLog>>(http.get("/api/audit-logs?page=0&size=50")),
  });

  return (
    <>
      <PageHeader title="Audit log" description="Every important action, append-only." />
      <Card>
        <Table>
          <THead>
            <TR><TH>Action</TH><TH>Entity</TH><TH>Actor</TH><TH>Details</TH><TH>When</TH></TR>
          </THead>
          <TBody>
            {(data?.content ?? []).map((a) => (
              <TR key={a.id}>
                <TD className="font-medium">{a.action}</TD>
                <TD className="text-muted-foreground">{a.entityType ?? "—"}</TD>
                <TD className="font-mono text-xs text-muted-foreground">{a.actor}</TD>
                <TD className="max-w-[280px] truncate font-mono text-xs text-muted-foreground" title={a.metadata ?? ""}>
                  {a.metadata ?? "—"}
                </TD>
                <TD className="text-muted-foreground">{formatDate(a.createdAt)}</TD>
              </TR>
            ))}
            {(data?.content ?? []).length === 0 && (
              <TR><TD colSpan={5} className="py-10 text-center text-muted-foreground">No audit entries yet.</TD></TR>
            )}
          </TBody>
        </Table>
      </Card>
    </>
  );
}
