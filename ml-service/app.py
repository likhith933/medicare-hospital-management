import os
from flask import Flask, request, jsonify
from flask_cors import CORS
import joblib
import numpy as np

app = Flask(__name__)
CORS(app) # Enable CORS for all routes

# Global variables for model resources
model = None
encoder_data = None
symptoms_list = []
classes_list = []
metadata_map = {}

def load_assets():
    global model, encoder_data, symptoms_list, classes_list, metadata_map
    model_path = os.path.join(os.path.dirname(__file__), "disease_model.pkl")
    encoder_path = os.path.join(os.path.dirname(__file__), "symptoms_encoder.pkl")
    
    if not os.path.exists(model_path) or not os.path.exists(encoder_path):
        print("Model or encoder file not found. Running training first...")
        from model_trainer import train_model
        train_model()
        
    model = joblib.load(model_path)
    encoder_data = joblib.load(encoder_path)
    symptoms_list = encoder_data["symptoms"]
    classes_list = encoder_data["classes"]
    metadata_map = encoder_data["disease_metadata"]
    print("ML assets loaded successfully.")

# Load models at startup
load_assets()

@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "UP", "message": "ML Service is healthy"}), 200

@app.route("/predict", methods=["POST"])
def predict():
    try:
        # Get request payload
        data = request.get_json()
        if not data or "symptoms" not in data:
            return jsonify({"error": "Bad request. Please provide 'symptoms' list in body."}), 400
            
        input_symptoms = data.get("symptoms", [])
        if not isinstance(input_symptoms, list):
            return jsonify({"error": "Symptoms must be provided as a JSON array of strings."}), 400
            
        # Create input features vector of length 132
        features = np.zeros(len(symptoms_list))
        matched_any = False
        for sym in input_symptoms:
            # Clean string and map
            sym_clean = sym.strip().lower().replace(" ", "_")
            if sym_clean in symptoms_list:
                idx = symptoms_list.index(sym_clean)
                features[idx] = 1
                matched_any = True
                
        # If no symptoms matched our 132 list, return a default response or try exact matches
        # We still run prediction since random forest can handle a zero vector, but let's log/flag it
        
        # Predict probability
        features = features.reshape(1, -1)
        prob = model.predict_proba(features)[0]
        pred_idx = np.argmax(prob)
        confidence = float(prob[pred_idx])
        predicted_disease = classes_list[pred_idx]
        
        # Retrieve metadata for the predicted disease
        # Normalize keys in metadata_map to find match
        disease_metadata = metadata_map.get(predicted_disease)
        if not disease_metadata:
            # Fallback check for keys with trailing whitespaces (e.g. 'Diabetes ', 'Hypertension ')
            for key, val in metadata_map.items():
                if key.strip().lower() == predicted_disease.strip().lower():
                    disease_metadata = val
                    break
                    
        if not disease_metadata:
            disease_metadata = {
                "specialization": "General Physician",
                "severity": "Medium",
                "precautions": ["Consult doctor", "Stay hydrated", "Get adequate rest"]
            }
            
        response = {
            "disease": predicted_disease.strip(),
            "confidence": round(confidence, 2),
            "specialization": disease_metadata["specialization"],
            "precautions": disease_metadata["precautions"],
            "severity": disease_metadata["severity"]
        }
        
        return jsonify(response), 200
        
    except Exception as e:
        print(f"Prediction Error: {str(e)}")
        return jsonify({"error": f"An error occurred during prediction: {str(e)}"}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True)
