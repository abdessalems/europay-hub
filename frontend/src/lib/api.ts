import axios from "axios";
import type { ApiResponse } from "./types";

const TOKEN_KEY = "europay_token";

export const tokenStore = {
  get: () => localStorage.getItem(TOKEN_KEY),
  set: (t: string) => localStorage.setItem(TOKEN_KEY, t),
  clear: () => localStorage.removeItem(TOKEN_KEY),
};

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? "http://localhost:8081",
  headers: { "Content-Type": "application/json" },
});

http.interceptors.request.use((config) => {
  const token = tokenStore.get();
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && !error.config?.url?.includes("/auth/")) {
      tokenStore.clear();
      if (location.pathname !== "/login") location.href = "/login";
    }
    const apiError = error.response?.data?.error;
    return Promise.reject(new Error(apiError?.message ?? error.message ?? "Request failed"));
  }
);

/** Unwrap the { success, data } envelope, throwing on failure. */
export async function unwrap<T>(promise: Promise<{ data: ApiResponse<T> }>): Promise<T> {
  const res = await promise;
  if (!res.data.success || res.data.data === null) {
    throw new Error(res.data.error?.message ?? "Request failed");
  }
  return res.data.data;
}
