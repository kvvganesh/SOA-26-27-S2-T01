import joblib
import pandas as pd

model = joblib.load("random_forest_model.joblib")


new_login = pd.DataFrame(
    [[1, 1, 1, 0]],
    columns=[
        "failed_attempts",
        "trusted_device",
        "trusted_location",
        "unusual_time"
    ]
)

prediction= model.predict(new_login)
probability= model.predict_proba(new_login)
print(f"Predicted risk level: {prediction[0]}")

print("Prediction probabilities:")
for risk, prob in zip(model.classes_, probability[0]):
    print(f"Risk level: {risk}, Probability: {prob:.2%}")
