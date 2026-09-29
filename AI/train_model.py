import pandas as pd
import joblib

from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report, accuracy_score


# -----------------------------
# 1. Training Dataset
# -----------------------------

data = {
    "failed_attempts": [
        0, 0, 1, 1, 2,
        1, 2, 3, 4, 5,
        3, 4, 5, 6, 7
    ],

    "trusted_device": [
        1, 1, 1, 1, 1,
        0, 1, 0, 0, 0,
        1, 0, 0, 0, 0
    ],

    "trusted_location": [
        1, 1, 1, 1, 1,
        1, 0, 1, 0, 0,
        0, 0, 0, 0, 0
    ],

    "unusual_time": [
        0, 0, 0, 1, 0,
        0, 1, 0, 1, 1,
        1, 1, 1, 1, 1
    ],

    "password_failed": [
        0, 0, 1, 1, 1,
        1, 1, 1, 1, 1,
        1, 1, 1, 1, 1
    ],

    "risk": [
        "LOW",
        "LOW",
        "MEDIUM",
        "MEDIUM",
        "MEDIUM",
        "MEDIUM",
        "MEDIUM",
        "MEDIUM",
        "HIGH",
        "HIGH",
        "HIGH",
        "HIGH",
        "HIGH",
        "HIGH",
        "HIGH"
    ]
}


df = pd.DataFrame(data)

print("Training dataset:")
print(df)


# -----------------------------
# 2. Separate Features and Label
# -----------------------------

X = df[
    [
        "failed_attempts",
        "trusted_device",
        "trusted_location",
        "unusual_time",
        "password_failed"
    ]
]

y = df["risk"]


# -----------------------------
# 3. Split Dataset
# -----------------------------

X_train, X_test, y_train, y_test = train_test_split(
    X,
    y,
    test_size=0.2,
    random_state=42,
    stratify=y
)


# -----------------------------
# 4. Create Random Forest
# -----------------------------

model = RandomForestClassifier(
    n_estimators=100,
    random_state=42
)


# -----------------------------
# 5. Train
# -----------------------------

model.fit(X_train, y_train)

print("\nFeatures used by the model:")
print(model.feature_names_in_)
# -----------------------------
# 6. Evaluate
# -----------------------------

y_pred = model.predict(X_test)

print("\nAccuracy:")
print(accuracy_score(y_test, y_pred))

print("\nClassification Report:")
print(classification_report(y_test, y_pred, zero_division=0))


# -----------------------------
# 7. Save Model
# -----------------------------

joblib.dump(
    model,
    "random_forest_model.joblib"
)

print("\nModel saved successfully.")