import type { PaymentMethod } from "./types";

/** All payment methods offered by the platform (order matters for the picker). */
export const PAYMENT_METHODS: PaymentMethod[] = [
  "VISA",
  "MASTERCARD",
  "BANCONTACT",
  "WERO",
  "SEPA_INSTANT",
  "PAYPAL",
  "APPLE_PAY",
];

export const METHOD_LABELS: Record<PaymentMethod, string> = {
  VISA: "Visa",
  MASTERCARD: "Mastercard",
  BANCONTACT: "Bancontact",
  WERO: "Wero",
  SEPA_INSTANT: "SEPA Instant",
  PAYPAL: "PayPal",
  APPLE_PAY: "Apple Pay",
};
