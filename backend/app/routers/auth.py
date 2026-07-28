from fastapi import APIRouter, HTTPException, Depends, status
from pydantic import BaseModel, EmailStr
from typing import Optional
from app.core.security import create_access_token

router = APIRouter(prefix="/auth", tags=["Authentication"])

class LoginRequest(BaseModel):
    email: EmailStr
    password: str

class RegisterRequest(BaseModel):
    name: str
    email: EmailStr
    password: str
    company: str
    role: str

class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    user_id: str
    role: str

@router.post("/login", response_model=TokenResponse)
def login(request: LoginRequest):
    if request.email == "marcus.vance@sitemind.ai" and request.password == "sitemind2026":
        token = create_access_token(subject="user_101")
        return TokenResponse(
            access_token=token,
            user_id="user_101",
            role="Senior Site Safety Engineer & Superintendent"
        )
    token = create_access_token(subject="user_gen")
    return TokenResponse(
        access_token=token,
        user_id="user_gen",
        role="Site Inspector"
    )

@router.post("/register", response_model=TokenResponse)
def register(request: RegisterRequest):
    token = create_access_token(subject=f"user_{request.email}")
    return TokenResponse(
        access_token=token,
        user_id=f"user_{request.email}",
        role=request.role
    )

@router.get("/me")
def get_current_user():
    return {
        "id": "user_101",
        "name": "Marcus Vance",
        "email": "marcus.vance@sitemind.ai",
        "role": "Senior Site Safety Engineer & Superintendent",
        "company": "BuildTech Global Engineering",
        "glasses": "Ray-Ban Meta Smart Glasses (Gen 2)"
    }
