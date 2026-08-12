from fastapi import APIRouter, HTTPException, Depends, status
from pydantic import BaseModel, EmailStr
from typing import Optional
from sqlalchemy.orm import Session
from app.db.session import get_db
from app.models.models import User
from app.core.security import create_access_token, verify_password, get_password_hash

router = APIRouter(prefix="/auth", tags=["Authentication"])

class LoginRequest(BaseModel):
    email: EmailStr
    password: str

class RegisterRequest(BaseModel):
    name: str
    email: EmailStr
    password: str
    company: Optional[str] = "BuildTech Global"
    role: Optional[str] = "SUPERVISOR"
    job_title: Optional[str] = "Safety Inspector"

class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    user_id: str
    name: str
    role: str

@router.post("/login", response_model=TokenResponse)
def login(request: LoginRequest, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.email == request.email).first()
    if not user or not verify_password(request.password, user.hashed_password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect email or password"
        )
    
    token = create_access_token(subject=user.id)
    return TokenResponse(
        access_token=token,
        user_id=user.id,
        name=user.name,
        role=user.role
    )

@router.post("/register", response_model=TokenResponse)
def register(request: RegisterRequest, db: Session = Depends(get_db)):
    existing_user = db.query(User).filter(User.email == request.email).first()
    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="User with this email already exists"
        )
    
    user_id = f"user_{request.email.split('@')[0]}"
    new_user = User(
        id=user_id,
        email=request.email,
        hashed_password=get_password_hash(request.password),
        name=request.name,
        role=request.role,
        job_title=request.job_title,
        company=request.company
    )
    db.add(new_user)
    db.commit()
    db.refresh(new_user)

    token = create_access_token(subject=new_user.id)
    return TokenResponse(
        access_token=token,
        user_id=new_user.id,
        name=new_user.name,
        role=new_user.role
    )

@router.get("/me")
def get_current_user(db: Session = Depends(get_db)):
    # Return first user or default profile
    user = db.query(User).first()
    if user:
        return {
            "id": user.id,
            "name": user.name,
            "email": user.email,
            "role": user.role,
            "job_title": user.job_title,
            "company": user.company
        }
    return {
        "id": "user_101",
        "name": "Marcus Vance",
        "email": "marcus.vance@sitemind.ai",
        "role": "SUPERVISOR",
        "job_title": "Senior Safety Inspector",
        "company": "BuildTech Global Engineering"
    }
