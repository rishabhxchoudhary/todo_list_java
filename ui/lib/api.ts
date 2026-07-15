const ACCESS_KEY = "accessToken";
const REFRESH_KEY = "refreshToken";

export const tokenStore = {
    getAccess: () => localStorage.getItem(ACCESS_KEY),
    getRefresh: () => localStorage.getItem(REFRESH_KEY),
    set: (accessToken: string, refreshToken: string) => {
        localStorage.setItem(ACCESS_KEY, accessToken);
        localStorage.setItem(REFRESH_KEY, refreshToken);
    },
    clear: () => {
        localStorage.removeItem(ACCESS_KEY);
        localStorage.removeItem(REFRESH_KEY);
    },
};

let refreshPromise: Promise<boolean> | null = null;

async function doRefresh(): Promise<boolean> {
    if (refreshPromise) return refreshPromise;

    refreshPromise = (async () => {
        try {
            const res = await fetch(`${API_BASE}/api/auth/refresh`, {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    refreshToken: tokenStore.getRefresh()
                })
            });
            if (!res.ok) return false;
            const data = await res.json();
            tokenStore.set(data.accessToken, data.refreshToken);
            return true;
        } finally {
            refreshPromise = null;
        }
    })();
    return refreshPromise;
}

const API_BASE = "http://localhost:8080";

export async function apiFetch(path: string, method: RequestInit["method"], body: any, isRetry: boolean = false) {
    const accessToken = tokenStore.getAccess();
    try {
        const response = await fetch(`${API_BASE}${path}`, {
            method,
            headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${accessToken}` },
            ...(body !== undefined && { body: JSON.stringify(body) }),
        });

        if (response.status == 401 && !isRetry) {
            const response2 = await doRefresh();
            if (response2) {
                return await apiFetch(path, method, body, true)
            } else {
                tokenStore.clear();
                window.location.href = "/login";
                throw new Error("Session expired");
            }
        }

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        if (response.status === 204) return null;
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Fetch failed:', error);
        throw error;
    }
}

export async function login(username: string, password: string) {
  const res = await fetch(`${API_BASE}/api/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password }),
  });
  if (!res.ok) throw new Error("Invalid credentials");
  const data = await res.json();
  tokenStore.set(data.accessToken, data.refreshToken);
  return data;
}

export async function signup(username: string, password: string) {
  const res = await fetch(`${API_BASE}/api/auth/signup`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password }),
  });
  if (!res.ok) {
    if (res.status === 409) throw new Error("Username already taken");
    throw new Error("Signup failed");
  }
  return res.json();
}

export async function logout() {
  try {
    await apiFetch("/api/auth/logout", "POST", undefined);  // authenticated → apiFetch
  } finally {
    tokenStore.clear();
  }
}