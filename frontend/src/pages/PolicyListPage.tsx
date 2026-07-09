import PolicyDetailModal from "@/features/policy/componenets/PolicyDetailModal";
import { useState } from "react";

type PolicySummary = {
    id: number;
    title: string;
    description: string;
    organization: string;
}

const PolicyListPage = () => {
    const [selectedPolicyId, setSelectedPolicyId] = useState<number | null>(null)

    const policies: PolicySummary[] = [
        {
            id: 1,
            title: "청년 취업 지원 정책",
            description: "청년의 취업 준비를 지원합니다.",
            organization: "고용노동부",
        },
    ];

    return (
        <>
            <section>
                {policies.map((policy) => (
                    <button
                        key={policy.id}
                        type="button"
                        onClick={() => setSelectedPolicyId(policy.id)}
                        className="policy-card"
                    >
                        <h3>{policy.title}</h3>
                        <p>{policy.description}</p>
                        <span>{policy.organization}</span>
                    </button>
                ))}
            </section>

            {selectedPolicyId && (
                <PolicyDetailModal
                    policyId={selectedPolicyId}
                    onClose={() => setSelectedPolicyId(null)}
                />
            )}
        </>
    )
}

export default PolicyListPage