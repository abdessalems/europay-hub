export function formatMoney(amount: number, currency = "EUR") {
  return new Intl.NumberFormat("en-IE", { style: "currency", currency }).format(amount);
}

export function formatDate(iso: string) {
  return new Date(iso).toLocaleString("en-GB", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

/** Maps a domain status to a badge tone. */
export function statusTone(status: string): "success" | "warning" | "danger" | "info" | "neutral" {
  switch (status) {
    case "SUCCESS":
    case "PAID":
    case "SETTLED":
    case "AUTHORIZED":
    case "ACTIVE":
      return "success";
    case "PENDING":
    case "CREATED":
      return "warning";
    case "FAILED":
    case "EXPIRED":
    case "CANCELLED":
    case "REVOKED":
      return "danger";
    case "REFUNDED":
      return "info";
    default:
      return "neutral";
  }
}
