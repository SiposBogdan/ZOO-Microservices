// src/utils/jwt.js

/**
 * Decode a JWT without external libraries.
 * Splits on '.', decodes the payload, and parses JSON.
 */
export function decodeJwt(token) {
  try {
    const parts = token.split('.');
    if (parts.length !== 3) return null;

    const payload = parts[1];
    // Convert from Base64Url to Base64
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    // Decode to string
    const json = atob(base64);
    return JSON.parse(json);
  } catch (e) {
    console.error('Failed to decode JWT:', e);
    return null;
  }
}
