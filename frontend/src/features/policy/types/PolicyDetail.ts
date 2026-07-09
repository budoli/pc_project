export type PolicyDetail = {
  id: number;
  externalId: string;
  title: string;
  description: string;
  organization: string;
  applyStartDate: string | null;
  applyEndDate: string | null;
  region: string;
  category: string;
  sourceUrl: string;
  status: string;
  condition: {
    minAge: number | null;
    maxAge: number | null;
    incomeCondition: string | null;
    jobCondition: string | null;
    educationCondition: string | null;
    additionalCondition: string | null;
    excludedTarget: string | null;
  } | null;
  document: {
    sourceType: string;
    summary: string | null;
    rawText: string | null;
  } | null;
};