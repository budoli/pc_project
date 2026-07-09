export type ChatRole = "user" | "assistant";

export type ChatPolicySummary = {
  id: number;
  title: string;
  description: string | null;
  organization: string | null;
  category: string | null;
};

export type ChatMessage = {
  id: string;
  role: ChatRole;
  content: string;
  policies?: ChatPolicySummary[];
};