import httpClient from "./HttpClient";

const unwrapData = (response) =>
  response && Object.prototype.hasOwnProperty.call(response, "data")
    ? response.data
    : response;

export const getApiErrorMessage = (error) => {
  const response = error?.response?.data;
  // If backend returned structured validation data, return it as an object
  if (response?.data && typeof response.data === "object") {
    return response.data;
  }

  // Map known DB unique-constraint messages to a friendly validation message
  const rawMessage = response?.message || error?.message;
  if (typeof rawMessage === "string") {
    const lower = rawMessage.toLowerCase();
    if (lower.includes("unique index") || lower.includes("unique index or primary key") || lower.includes("unique constraint") || lower.includes("duplicate")) {
      return { duplicate: "Customer with same firstName, lastName and dateOfBirth already exists" };
    }
    return rawMessage;
  }

  return "Request failed";
};

export const customersApi = {
  getAll: async () => {
    const res = await httpClient.get("/customers");
    return unwrapData(res.data);
  },
  create: async (customer) => {
    const res = await httpClient.post("/customers", customer);
    return unwrapData(res.data);
  },
};
