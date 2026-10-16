from .schemas import PolicyEmbeddingRequest


def build_policy_embedding_text(policy: PolicyEmbeddingRequest) -> str:
    return f"""
    제목: {policy.title}
    설명: {policy.description}
    기관: {policy.organization}
    분야: {policy.category}
    """


def build_policy_payload(policy: PolicyEmbeddingRequest) -> dict:
    return {
        "policyId": policy.policyId,
        "title": policy.title,
        "description": policy.description,
        "organization": policy.organization,
        "region": policy.region,
        "category": policy.category,
    }


def map_qdrant_result_to_policy(result) -> dict:
    return {
        "score": result.score,
        "policyId": result.payload.get("policyId"),
        "title": result.payload.get("title"),
        "description": result.payload.get("description"),
        "organization": result.payload.get("organization"),
        "region": result.payload.get("region"),
        "category": result.payload.get("category"),
    }
