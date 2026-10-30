import os
from dataclasses import dataclass

import psycopg

import core.config  # noqa: F401  ai/.env 로드


@dataclass(frozen=True)
class Policy:
    id: int
    external_id: str
    title: str
    description: str | None
    organization: str | None
    region: str | None
    category: str | None
    raw_text: str | None


POLICY_QUERY = """
    SELECT p.id, p.external_id, p.title, p.description, p.organization,
           p.region, p.category, d.raw_text
    FROM policies p
    LEFT JOIN policy_documents d ON d.policy_id = p.id
    ORDER BY p.id
"""


def connect():
    return psycopg.connect(
        host=os.getenv("DB_HOST", "localhost"),
        port=int(os.getenv("DB_PORT", "5432")),
        dbname=os.getenv("DB_NAME"),
        user=os.getenv("DB_USERNAME"),
        password=os.getenv("DB_PASSWORD"),
    )


def load_policies() -> list[Policy]:
    with connect() as conn, conn.cursor() as cur:
        cur.execute(POLICY_QUERY)
        return [Policy(*row) for row in cur.fetchall()]
