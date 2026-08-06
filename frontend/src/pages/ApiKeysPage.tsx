import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Plus, Copy, Trash2 } from "lucide-react";
import { http, unwrap } from "@/lib/api";
import type { ApiKeyCreated, ApiKeySummary } from "@/lib/types";
import { formatDate } from "@/lib/format";
import { Card, CardContent } from "@/components/ui/card";
import { Table, THead, TBody, TR, TH, TD } from "@/components/ui/table";
import { StatusBadge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { PageHeader } from "@/components/PageHeader";

export function ApiKeysPage() {
  const qc = useQueryClient();
  const [name, setName] = useState("");
  const [created, setCreated] = useState<ApiKeyCreated | null>(null);

  const { data } = useQuery({
    queryKey: ["api-keys"],
    queryFn: () => unwrap<ApiKeySummary[]>(http.get("/api/merchants/me/api-keys")),
  });

  const create = useMutation({
    mutationFn: () => unwrap<ApiKeyCreated>(http.post("/api/merchants/me/api-keys", { name })),
    onSuccess: (key) => { setCreated(key); setName(""); qc.invalidateQueries({ queryKey: ["api-keys"] }); },
  });

  const revoke = useMutation({
    mutationFn: (id: string) => http.delete(`/api/merchants/me/api-keys/${id}`),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["api-keys"] }),
  });

  return (
    <>
      <PageHeader title="API Keys" description="Server-to-server credentials for the payment API." />

      <Card className="mb-6">
        <CardContent className="p-6">
          <form onSubmit={(e) => { e.preventDefault(); create.mutate(); }} className="flex flex-wrap items-end gap-3">
            <div className="space-y-1.5">
              <Label>Key name</Label>
              <Input value={name} onChange={(e) => setName(e.target.value)} placeholder="Production server" required />
            </div>
            <Button type="submit" disabled={create.isPending}><Plus /> Generate key</Button>
          </form>

          {created && (
            <div className="mt-4 rounded-lg border border-primary/30 bg-primary/10 p-4">
              <p className="text-sm font-medium text-primary">Copy your secret now — it won't be shown again.</p>
              <div className="mt-2 flex items-center gap-2">
                <code className="flex-1 overflow-x-auto rounded bg-background px-3 py-2 font-mono text-sm">{created.secretKey}</code>
                <Button size="icon" variant="outline" onClick={() => navigator.clipboard.writeText(created.secretKey)}>
                  <Copy />
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>

      <Card>
        <Table>
          <THead>
            <TR><TH>Name</TH><TH>Prefix</TH><TH>Status</TH><TH>Created</TH><TH className="text-right">Actions</TH></TR>
          </THead>
          <TBody>
            {(data ?? []).map((k) => (
              <TR key={k.id}>
                <TD className="font-medium">{k.name}</TD>
                <TD className="font-mono text-xs">{k.prefix}…</TD>
                <TD><StatusBadge status={k.status} /></TD>
                <TD className="text-muted-foreground">{formatDate(k.createdAt)}</TD>
                <TD className="text-right">
                  {k.status === "ACTIVE" && (
                    <Button size="sm" variant="destructive" disabled={revoke.isPending}
                      onClick={() => revoke.mutate(k.id)}>
                      <Trash2 /> Revoke
                    </Button>
                  )}
                </TD>
              </TR>
            ))}
            {(data ?? []).length === 0 && (
              <TR><TD colSpan={5} className="py-10 text-center text-muted-foreground">No API keys yet.</TD></TR>
            )}
          </TBody>
        </Table>
      </Card>
    </>
  );
}
