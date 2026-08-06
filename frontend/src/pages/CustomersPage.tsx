import { useQuery } from "@tanstack/react-query";
import { http, unwrap } from "@/lib/api";
import type { Customer, Page } from "@/lib/types";
import { formatDate } from "@/lib/format";
import { Card } from "@/components/ui/card";
import { Table, THead, TBody, TR, TH, TD } from "@/components/ui/table";
import { PageHeader } from "@/components/PageHeader";

export function CustomersPage() {
  const { data } = useQuery({
    queryKey: ["customers", "list"],
    queryFn: () => unwrap<Page<Customer>>(http.get("/api/customers?page=0&size=50")),
  });

  return (
    <>
      <PageHeader title="Customers" description="Everyone who has ordered from you." />
      <Card>
        <Table>
          <THead>
            <TR><TH>Name</TH><TH>Email</TH><TH>Since</TH></TR>
          </THead>
          <TBody>
            {(data?.content ?? []).map((c) => (
              <TR key={c.id}>
                <TD className="font-medium">{c.fullName}</TD>
                <TD className="text-muted-foreground">{c.email}</TD>
                <TD className="text-muted-foreground">{formatDate(c.createdAt)}</TD>
              </TR>
            ))}
            {(data?.content ?? []).length === 0 && (
              <TR><TD colSpan={3} className="py-10 text-center text-muted-foreground">No customers yet.</TD></TR>
            )}
          </TBody>
        </Table>
      </Card>
    </>
  );
}
