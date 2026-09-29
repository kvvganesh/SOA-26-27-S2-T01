import joblib
import pandas as pd

from fastapi import FastAPI
from pydantic import BaseModel


class LoginRequest(BaseModel):
    failed_attempts: int
    trusted_device: int
    trusted_location: int
    unusual_time: int
    password_failed: int


app = FastAPI()

model = joblib.load("random_forest_model.joblib")


@app.get("/")
def home():
    return {
        "message": "AI risk assessment service is running"
    }


@app.post("/predict")
def predict_risk(request: LoginRequest):

    login_data = pd.DataFrame(
        [[
            request.failed_attempts,
            request.trusted_device,
            request.trusted_location,
            request.unusual_time,
            request.password_failed
        ]],
        columns=[
            "failed_attempts",
            "trusted_device",
            "trusted_location",
            "unusual_time",
            "password_failed"
        ]
    )

    prediction = model.predict(login_data)[0]

    probabilities = model.predict_proba(login_data)[0]

    probability_map = {
        risk: round(float(prob), 4)
        for risk, prob in zip(
            model.classes_,
            probabilities
        )
    }

    return {
        "predicted_risk": prediction,
        "probabilities": probability_map
    }