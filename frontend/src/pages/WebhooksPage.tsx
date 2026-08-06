import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Copy } from "lucide-react";
import { http, unwrap } from "@/lib/api";
import type { Page, WebhookEndpoint, WebhookEvent } from "@/lib/types";
import { formatDate } from "@/lib/format";
import { Card, CardContent } from "@/components/ui/card";
import { Table, THead, TBody, TR, TH, TD } from "@/components/ui/table";
import { StatusBadge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { PageHeader } from "@/components/PageHeader";

export function WebhooksPage() {
  const qc = useQueryClient();
  const [url, setUrl] = useState("");
  const [freshSecret, setFreshSecret] = useState<string | null>(null);

  const endpoint = useQuery({
    queryKey: ["webhook-endpoint"],
    queryFn: () => unwrap<WebhookEndpoint>(http.get("/api/webhooks/endpoint")),
    retry: false,
  });

  const events = useQuery({
    queryKey: ["webhook-events"],
    queryFn: () => unwrap<Page<WebhookEvent>>(http.get("/api/webhooks/events?page=0&size=25")),
  });

  const save = useMutation({
    mutationFn: () => unwrap<WebhookEndpoint>(http.put("/api/webhooks/endpoint", { url })),
    onSuccess: (e) => {
      setFreshSecret(e.secret);
      setUrl("");
      qc.invalidateQueries({ queryKey: ["webhook-endpoint"] });
    },
  });

  const current = endpoint.data;

  return (
    <>
      <PageHeader title="Webhooks" description="Get notified of payment events on your server." />

      <Card className="mb-6">
        <CardContent className="p-6">
          {current && (
            <p className="mb-4 text-sm">
              Current endpoint:{" "}
              <span className="font-mono">{current.url}</span>{" "}
              <StatusBadge status={current.active ? "ACTIVE" : "DISABLED"} />
            </p>
          )}
          <form onSubmit={(e) => { e.preventDefault(); save.mutate(); }} className="flex flex-wrap items-end gap-3">
            <div className="min-w-[320px] flex-1 space-y-1.5">
              <Label>Callback URL</Label>
              <Input type="url" value={url} onChange={(e) => setUrl(e.target.value)}
                placeholder="https://my-shop.eu/webhooks/europay" required />
            </div>
            <Button type="submit" disabled={save.isPending}>
              {save.isPending ? "Saving…" : current ? "Update endpoint" : "Add endpoint"}
            </Button>
          </form>

          {freshSecret && (
            <div className="mt-4 rounded-lg border border-primary/30 bg-primary/10 p-4">
              <p className="text-sm font-medium text-primary">Signing secret — copy it now, it won't be shown again.</p>
              <div className="mt-2 flex items-center gap-2">
                <code className="flex-1 overflow-x-auto rounded bg-background px-3 py-2 font-mono text-sm">{freshSecret}</code>
                <Button size="icon" variant="outline" onClick={() => navigator.clipboard.writeText(freshSecret)}>
                  <Copy />
                </Button>
              </div>
              <p className="mt-2 text-xs text-muted-foreground">
                Verify each call with header <span className="font-mono">X-EuroPay-Signature: sha256=HMAC(secret, body)</span>.
              </p>
            </div>
          )}
        </CardContent>
      </Card>

      <Card>
        <div className="border-b border-border px-6 py-4"><h2 className="font-semibold">Delivery events</h2></div>
        <Table>
          <THead>
            <TR><TH>Event</TH><TH>Status</TH><TH>Attempts</TH><TH>Last code</TH><TH>Created</TH></TR>
          </THead>
          <TBody>
            {(events.data?.content ?? []).map((e) => (
              <TR key={e.id}>
                <TD className="font-mono text-xs">{e.eventType}</TD>
                <TD><StatusBadge status={e.status} /></TD>
                <TD>{e.attempts}</TD>
                <TD className="text-muted-foreground">{e.lastStatusCode ?? "—"}</TD>
                <TD className="text-muted-foreground">{formatDate(e.createdAt)}</TD>
              </TR>
            ))}
            {(events.data?.content ?? []).length === 0 && (
              <TR><TD colSpan={5} className="py-10 text-center text-muted-foreground">No webhook events yet.</TD></TR>
            )}
          </TBody>
        </Table>
      </Card>
    </>
  );
}
