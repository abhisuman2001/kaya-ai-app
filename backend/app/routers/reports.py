from fastapi import APIRouter
from pydantic import BaseModel
from typing import List

router = APIRouter(prefix="/reports", tags=["Reports"])

class DailyReport(BaseModel):
    id: str
    shiftDate: str
    author: str
    summary: str
    safetyScorePct: float
    fieldSnapshotsCount: int

@router.get("", response_model=List[DailyReport])
def get_site_reports():
    return [
        DailyReport(
            id="report_dpr_42",
            shiftDate="2026-07-25",
            author="Marcus Vance",
            summary="Level 18 Deck Pour completed with 18 automated field snapshots & rebar audit score of 98%.",
            safetyScorePct=98.0,
            fieldSnapshotsCount=18
        ),
        DailyReport(
            id="report_dpr_41",
            shiftDate="2026-07-24",
            author="Marcus Vance",
            summary="MEP Riser conduit inspection completed. 1 PPE warning issued and resolved.",
            safetyScorePct=95.5,
            fieldSnapshotsCount=14
        )
    ]

@router.post("/generate-dpr")
def generate_dpr_report():
    return {
        "status": "GENERATED",
        "report_id": "report_dpr_43",
        "ai_summary": "Auto-compiled shift report with Ray-Ban glasses vision telemetry & audit log.",
        "download_pdf_url": "/api/v1/reports/report_dpr_43.pdf"
    }
