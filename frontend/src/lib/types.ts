export interface ApiResponse<T> {
  success: boolean;
  data: T | null;
  error: { code: string; message: string; path: string; details: { field: string; message: string }[] } | null;
  timestamp: string;
}

export interface Page<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface AuthResult {
  accessToken: string;
  tokenType: string;
  expiresInSeconds: number;
  role: string;
}

export interface Merchant {
  id: string;
  legalName: string;
  email: string;
  status: string;
  createdAt: string;
}

export interface ApiKeySummary {
  id: string;
  name: string;
  prefix: string;
  status: string;
  createdAt: string;
  lastUsedAt: string | null;
  expiresAt: string | null;
}

export interface ApiKeyCreated extends ApiKeySummary {
  secretKey: string;
}

export interface Order {
  id: string;
  reference: string;
  status: string;
  amount: number;
  currency: string;
  customerId: string;
  createdAt: string;
}

export interface Customer {
  id: string;
  email: string;
  fullName: string;
  createdAt: string;
}

export type PaymentMethod =
  | "WERO"
  | "BANCONTACT"
  | "VISA"
  | "MASTERCARD"
  | "SEPA_INSTANT"
  | "PAYPAL"
  | "APPLE_PAY";

export interface Payment {
  id: string;
  orderId: string;
  paymentMethod: string;
  amount: number;
  currency: string;
  status: string;
  providerReference: string | null;
  failureReason: string | null;
  createdAt: string;
}
