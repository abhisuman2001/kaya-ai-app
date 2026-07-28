from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import List, Optional

router = APIRouter(prefix="/projects", tags=["Projects"])

class ProjectItem(BaseModel):
    id: str
    name: str
    location: String
    level: str
    progressPct: float
    activeWorkers: int

class ProjectCreate(BaseModel):
    name: str
    location: str
    level: str

@router.get("", response_model=List[dict])
def list_projects():
    return [
        {
            "id": "proj_01",
            "name": "Metro Tower Construction",
            "location": "Downtown Financial District, Bay Area",
            "level": "Level 18 West Slab",
            "progressPct": 68.5,
            "activeWorkers": 42
        },
        {
            "id": "proj_02",
            "name": "Skyline Commercial Hub",
            "location": "North River Deck",
            "level": "Level 04 Podium",
            "progressPct": 34.0,
            "activeWorkers": 28
        }
    ]

@router.post("", response_model=dict)
def create_project(proj: ProjectCreate):
    return {
        "id": "proj_new",
        "name": proj.name,
        "location": proj.location,
        "level": proj.level,
        "status": "CREATED",
        "progressPct": 0.0
    }

@router.get("/{project_id}/bim")
def get_project_bim_model(project_id: str):
    return {
        "project_id": project_id,
        "ifc_model_url": "s3://sitemind-bim/models/metro_tower_l18.ifc",
        "clash_count": 3,
        "last_synced": "2026-07-26T00:15:00Z"
    }
