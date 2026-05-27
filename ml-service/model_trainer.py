import os
import joblib
import numpy as np
import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder

# 1. Complete list of 132 symptoms as features
SYMPTOMS = [
    "itching", "skin_rash", "nodal_skin_eruptions", "continuous_sneezing", "shivering", "chills", "joint_pain",
    "stomach_pain", "acidity", "ulcers_on_tongue", "muscle_wasting", "vomiting", "burning_micturition",
    "spotting_ urination", "fatigue", "weight_gain", "anxiety", "cold_hands_and_feets", "mood_swings",
    "weight_loss", "restlessness", "lethargy", "patches_in_throat", "irregular_sugar_level", "cough",
    "high_fever", "sunken_eyes", "breathlessness", "sweating", "dehydration", "indigestion", "headache",
    "yellowish_skin", "dark_urine", "nausea", "loss_of_appetite", "pain_behind_the_eyes", "back_pain",
    "constipation", "abdominal_pain", "diarrhoea", "mild_fever", "yellow_urine", "yellowing_of_eyes",
    "acute_liver_failure", "fluid_overload", "swelling_of_stomach", "swelled_lymph_nodes", "malaise",
    "blurred_and_distorted_vision", "phlegm", "throat_irritation", "redness_of_eyes", "sinus_pressure",
    "runny_nose", "congestion", "chest_pain", "weakness_in_limbs", "fast_heart_rate", "pain_during_bowel_movements",
    "pain_in_anal_region", "bloody_stool", "irritation_in_anus", "neck_pain", "dizziness", "cramps",
    "bruising", "obesity", "swollen_legs", "swollen_blood_vessels", "puffy_face_and_eyes", "enlarged_thyroid",
    "brittle_nails", "swollen_extremeties", "excessive_hunger", "extra_marital_contacts", "drying_of_peels_and_sent_limes",
    "depression", "irritability", "muscle_pain", "altered_sensorium", "red_spots_over_body", "belly_pain",
    "abnormal_menstruation", "dischromic _patches", "watering_from_eyes", "increased_appetite", "polyuria",
    "family_history", "mucoid_sputum", "rusty_sputum", "lack_of_concentration", "visual_disturbances",
    "receiving_blood_transfusion", "receiving_unsterile_injections", "coma", "stomach_bleeding", "distention_of_abdomen",
    "history_of_alcohol_consumption", "blood_in_sputum", "prominent_veins_on_calf", "palpitations", "painful_walking",
    "pus_filled_pimples", "blackheads", "scurring", "skin_peeling", "silver_like_dusting", "small_dents_in_nails",
    "inflammatory_nails", "blister", "red_sore_around_nose", "yellow_crust_ooze", "bladder_discomfort",
    "foul_smell_of_urine", "continuous_feel_of_urine", "stiff_joints", "swelling_joints", "movement_stiffness",
    "hip_joint_pain", "knee_pain", "slurred_speech", "muscle_weakness", "stiff_neck", "loss_of_balance",
    "unsteadiness", "passage_of_gases", "internal_itching", "toxic_look_(typhos)"
]

# 2. Disease Mapping Metadata (41 diseases)
DISEASE_METADATA = {
    "Fungal infection": {
        "specialization": "Dermatologist", "severity": "Low",
        "precautions": ["bath twice", "use dettol", "keep dry", "use clean towels"],
        "symptoms": ["itching", "skin_rash", "nodal_skin_eruptions", "dischromic _patches"]
    },
    "Allergy": {
        "specialization": "Allergist", "severity": "Low",
        "precautions": ["avoid allergen", "take antihistamine", "keep surroundings clean", "wear mask"],
        "symptoms": ["continuous_sneezing", "shivering", "chills", "watering_from_eyes"]
    },
    "GERD": {
        "specialization": "Gastroenterologist", "severity": "Medium",
        "precautions": ["avoid fatty food", "eat smaller meals", "do not sleep after eating", "reduce caffeine"],
        "symptoms": ["acidity", "ulcers_on_tongue", "vomiting", "cough", "chest_pain"]
    },
    "Chronic cholestasis": {
        "specialization": "Gastroenterologist", "severity": "High",
        "precautions": ["consult doctor", "avoid alcohol", "low fat diet", "regular checkups"],
        "symptoms": ["vomiting", "yellowish_skin", "nausea", "loss_of_appetite", "yellowing_of_eyes"]
    },
    "Drug Reaction": {
        "specialization": "Allergist", "severity": "Medium",
        "precautions": ["stop medication", "consult doctor", "drink plenty of water", "monitor symptoms"],
        "symptoms": ["itching", "skin_rash", "stomach_pain", "burning_micturition"]
    },
    "Peptic ulcer diseae": {
        "specialization": "Gastroenterologist", "severity": "High",
        "precautions": ["avoid spicy food", "eat on time", "limit caffeine", "take antacids"],
        "symptoms": ["vomiting", "indigestion", "loss_of_appetite", "abdominal_pain", "passage_of_gases", "internal_itching"]
    },
    "AIDS": {
        "specialization": "Infectious Disease Specialist", "severity": "High",
        "precautions": ["safe sex", "do not share needles", "regular screening", "medication compliance"],
        "symptoms": ["muscle_wasting", "patches_in_throat", "high_fever", "extra_marital_contacts"]
    },
    "Diabetes ": {
        "specialization": "Endocrinologist", "severity": "High",
        "precautions": ["control diet", "regular exercise", "monitor sugar level", "medication compliance"],
        "symptoms": ["fatigue", "weight_loss", "restlessness", "lethargy", "irregular_sugar_level", "increased_appetite", "polyuria"]
    },
    "Gastroenteritis": {
        "specialization": "Gastroenterologist", "severity": "Medium",
        "precautions": ["stay hydrated", "eat light food", "wash hands", "avoid dairy"],
        "symptoms": ["vomiting", "dehydration", "diarrhoea"]
    },
    "Bronchial Asthma": {
        "specialization": "Pulmonologist", "severity": "High",
        "precautions": ["use inhaler", "avoid dust", "keep warm", "avoid cold food"],
        "symptoms": ["fatigue", "cough", "high_fever", "breathlessness", "family_history", "mucoid_sputum"]
    },
    "Hypertension ": {
        "specialization": "Cardiologist", "severity": "High",
        "precautions": ["low salt diet", "reduce stress", "regular exercise", "monitor blood pressure"],
        "symptoms": ["headache", "chest_pain", "dizziness", "loss_of_balance", "lack_of_concentration"]
    },
    "Migraine": {
        "specialization": "Neurologist", "severity": "Medium",
        "precautions": ["dim light", "avoid loud noise", "stay hydrated", "adequate sleep"],
        "symptoms": ["acidity", "indigestion", "headache", "blurred_and_distorted_vision", "excessive_hunger", "stiff_neck", "depression", "irritability", "visual_disturbances"]
    },
    "Cervical spondylosis": {
        "specialization": "Orthopedist", "severity": "Medium",
        "precautions": ["neck exercises", "maintain posture", "use orthopedic pillow", "consult physiotherapist"],
        "symptoms": ["back_pain", "neck_pain", "dizziness", "loss_of_balance"]
    },
    "Paralysis (brain hemorrhage)": {
        "specialization": "Neurologist", "severity": "High",
        "precautions": ["immediate hospitalization", "physiotherapy", "monitor blood pressure", "lifestyle change"],
        "symptoms": ["vomiting", "headache", "weakness_in_limbs", "altered_sensorium"]
    },
    "Jaundice": {
        "specialization": "Gastroenterologist", "severity": "High",
        "precautions": ["rest", "low fat diet", "stay hydrated", "avoid alcohol"],
        "symptoms": ["itching", "vomiting", "fatigue", "weight_loss", "high_fever", "yellowish_skin", "dark_urine", "yellowing_of_eyes"]
    },
    "Malaria": {
        "specialization": "Infectious Disease Specialist", "severity": "High",
        "precautions": ["use mosquito net", "wear full sleeves", "anti-malarial drugs", "remove stagnant water"],
        "symptoms": ["chills", "vomiting", "high_fever", "headache", "nausea", "muscle_pain"]
    },
    "Chicken pox": {
        "specialization": "Pediatrician", "severity": "Medium",
        "precautions": ["isolate patient", "vaccination", "do not scratch rashes", "use calamine lotion"],
        "symptoms": ["itching", "skin_rash", "fatigue", "lethargy", "high_fever", "headache", "loss_of_appetite", "mild_fever", "swelled_lymph_nodes", "red_spots_over_body"]
    },
    "Dengue": {
        "specialization": "Infectious Disease Specialist", "severity": "High",
        "precautions": ["stay hydrated", "use mosquito net", "wear full sleeves", "take paracetamol"],
        "symptoms": ["skin_rash", "chills", "joint_pain", "vomiting", "high_fever", "headache", "nausea", "loss_of_appetite", "pain_behind_the_eyes", "back_pain", "muscle_pain", "red_spots_over_body"]
    },
    "Typhoid": {
        "specialization": "Infectious Disease Specialist", "severity": "High",
        "precautions": ["drink boiled water", "eat hot food", "maintain hand hygiene", "take antibiotics"],
        "symptoms": ["chills", "vomiting", "fatigue", "high_fever", "headache", "nausea", "constipation", "abdominal_pain", "diarrhoea", "toxic_look_(typhos)"]
    },
    "hepatitis A": {
        "specialization": "Gastroenterologist", "severity": "High",
        "precautions": ["avoid alcohol", "vaccination", "hand hygiene", "low fat diet"],
        "symptoms": ["joint_pain", "vomiting", "fatigue", "yellowish_skin", "dark_urine", "nausea", "loss_of_appetite", "abdominal_pain", "yellowing_of_eyes", "mild_fever"]
    },
    "Hepatitis B": {
        "specialization": "Gastroenterologist", "severity": "High",
        "precautions": ["vaccination", "safe sex", "avoid shared needles", "regular liver checkup"],
        "symptoms": ["itching", "fatigue", "yellowish_skin", "dark_urine", "loss_of_appetite", "abdominal_pain", "yellowing_of_eyes", "receiving_blood_transfusion", "receiving_unsterile_injections"]
    },
    "Hepatitis C": {
        "specialization": "Gastroenterologist", "severity": "High",
        "precautions": ["avoid shared needles", "safe sex", "regular liver checkup", "medical compliance"],
        "symptoms": ["fatigue", "yellowish_skin", "nausea", "loss_of_appetite", "yellowing_of_eyes", "family_history"]
    },
    "Hepatitis D": {
        "specialization": "Gastroenterologist", "severity": "High",
        "precautions": ["hepatitis B vaccine", "avoid shared needles", "safe sex", "regular liver checkup"],
        "symptoms": ["joint_pain", "vomiting", "fatigue", "yellowish_skin", "dark_urine", "abdominal_pain", "yellowing_of_eyes"]
    },
    "Hepatitis E": {
        "specialization": "Gastroenterologist", "severity": "High",
        "precautions": ["drink clean water", "proper sanitation", "cook food well", "avoid raw food"],
        "symptoms": ["joint_pain", "vomiting", "fatigue", "high_fever", "yellowish_skin", "dark_urine", "nausea", "loss_of_appetite", "abdominal_pain", "yellowing_of_eyes", "acute_liver_failure", "coma", "stomach_bleeding"]
    },
    "Alcoholic hepatitis": {
        "specialization": "Gastroenterologist", "severity": "High",
        "precautions": ["stop alcohol", "consult doctor", "healthy diet", "liver support supplements"],
        "symptoms": ["vomiting", "yellowish_skin", "abdominal_pain", "swelling_of_stomach", "distention_of_abdomen", "history_of_alcohol_consumption", "fluid_overload"]
    },
    "Tuberculosis": {
        "specialization": "Pulmonologist", "severity": "High",
        "precautions": ["complete therapy course", "wear mask", "well ventilated room", "separate utensils"],
        "symptoms": ["chills", "vomiting", "fatigue", "weight_loss", "cough", "high_fever", "breathlessness", "sweating", "loss_of_appetite", "mild_fever", "swelled_lymph_nodes", "malaise", "phlegm", "chest_pain", "blood_in_sputum"]
    },
    "Common Cold": {
        "specialization": "General Physician", "severity": "Low",
        "precautions": ["drink hot liquids", "steam inhalation", "salt water gargle", "take rest"],
        "symptoms": ["continuous_sneezing", "chills", "fatigue", "cough", "high_fever", "headache", "throat_irritation", "redness_of_eyes", "sinus_pressure", "runny_nose", "congestion", "chest_pain", "muscle_pain"]
    },
    "Pneumonia": {
        "specialization": "Pulmonologist", "severity": "High",
        "precautions": ["rest", "stay hydrated", "consult doctor", "antibiotic compliance"],
        "symptoms": ["chills", "fatigue", "cough", "high_fever", "breathlessness", "sweating", "malaise", "phlegm", "chest_pain", "fast_heart_rate", "rusty_sputum"]
    },
    "Dimorphic hemmorhoids(piles)": {
        "specialization": "Proctologist", "severity": "Medium",
        "precautions": ["high fiber diet", "stay hydrated", "avoid straining", "sitz bath"],
        "symptoms": ["constipation", "pain_during_bowel_movements", "pain_in_anal_region", "bloody_stool", "irritation_in_anus"]
    },
    "Heart attack": {
        "specialization": "Cardiologist", "severity": "High",
        "precautions": ["call emergency", "chew aspirin", "keep calm", "cpr if unconscious"],
        "symptoms": ["vomiting", "breathlessness", "sweating", "chest_pain"]
    },
    "Varicose veins": {
        "specialization": "Vascular Surgeon", "severity": "Medium",
        "precautions": ["wear compression stockings", "elevate legs", "avoid standing long", "regular exercise"],
        "symptoms": ["fatigue", "cramps", "bruising", "swollen_legs", "swollen_blood_vessels", "prominent_veins_on_calf"]
    },
    "Hypothyroidism": {
        "specialization": "Endocrinologist", "severity": "Medium",
        "precautions": ["thyroid hormone checkup", "low calorie diet", "regular exercise", "medication compliance"],
        "symptoms": ["fatigue", "weight_gain", "cold_hands_and_feets", "mood_swings", "lethargy", "constipation", "neck_pain", "puffy_face_and_eyes", "enlarged_thyroid", "brittle_nails", "swollen_extremeties", "depression", "irritability", "abnormal_menstruation"]
    },
    "Hyperthyroidism": {
        "specialization": "Endocrinologist", "severity": "Medium",
        "precautions": ["medication compliance", "regular checkup", "avoid iodine food", "manage stress"],
        "symptoms": ["fatigue", "mood_swings", "weight_loss", "restlessness", "sweating", "diarrhoea", "fast_heart_rate", "excessive_hunger", "muscle_weakness", "irritability", "abnormal_menstruation"]
    },
    "Hypoglycemia": {
        "specialization": "Endocrinologist", "severity": "High",
        "precautions": ["eat sugar source", "regular meals", "monitor sugar level", "consult doctor"],
        "symptoms": ["vomiting", "fatigue", "anxiety", "sweating", "headache", "nausea", "dizziness", "excessive_hunger", "slurred_speech", "irritability", "palpitations"]
    },
    "Osteoarthristis": {
        "specialization": "Orthopedist", "severity": "Medium",
        "precautions": ["weight control", "gentle exercise", "pain relief gels", "hot/cold compress"],
        "symptoms": ["joint_pain", "neck_pain", "painful_walking", "hip_joint_pain", "knee_pain", "swelling_joints"]
    },
    "Arthritis": {
        "specialization": "Rheumatologist", "severity": "Medium",
        "precautions": ["anti-inflammatory diet", "regular exercise", "hot/cold pack", "consult rheumatologist"],
        "symptoms": ["muscle_weakness", "stiff_joints", "swelling_joints", "movement_stiffness", "painful_walking"]
    },
    "(vertigo) Paroymsal  Positional Vertigo": {
        "specialization": "Neurologist", "severity": "Medium",
        "precautions": ["avoid sudden head movement", "lie down when dizzy", "semont maneuver", "stay hydrated"],
        "symptoms": ["vomiting", "headache", "nausea", "dizziness", "loss_of_balance", "unsteadiness"]
    },
    "Acne": {
        "specialization": "Dermatologist", "severity": "Low",
        "precautions": ["wash face gently", "avoid oily cosmetics", "don't pop pimples", "stay hydrated"],
        "symptoms": ["skin_rash", "pus_filled_pimples", "blackheads", "scurring"]
    },
    "Urinary tract infection": {
        "specialization": "Urologist", "severity": "Medium",
        "precautions": ["drink water", "maintain hygiene", "cranberry juice", "take antibiotics"],
        "symptoms": ["burning_micturition", "bladder_discomfort", "foul_smell_of_urine", "continuous_feel_of_urine"]
    },
    "Psoriasis": {
        "specialization": "Dermatologist", "severity": "Low",
        "precautions": ["moisturize skin", "avoid triggers", "sun exposure in limit", "use mild soap"],
        "symptoms": ["skin_rash", "joint_pain", "skin_peeling", "silver_like_dusting", "small_dents_in_nails", "inflammatory_nails"]
    },
    "Impetigo": {
        "specialization": "Dermatologist", "severity": "Low",
        "precautions": ["antibiotic ointment", "keep sores clean", "wash clothes in hot water", "wash hands"],
        "symptoms": ["skin_rash", "high_fever", "blister", "red_sore_around_nose", "yellow_crust_ooze"]
    }
}

def generate_synthetic_data(num_samples=2500):
    print(f"Generating {num_samples} synthetic patient samples...")
    data = []
    diseases = list(DISEASE_METADATA.keys())
    
    for _ in range(num_samples):
        # 1. Pick a random disease
        disease = np.random.choice(diseases)
        metadata = DISEASE_METADATA[disease]
        typical_symptoms = metadata["symptoms"]
        
        # 2. Build feature vector (all zeros initially)
        row = {symptom: 0 for symptom in SYMPTOMS}
        
        # 3. Add typical symptoms with high probability (80-100%)
        active_count = 0
        for s in typical_symptoms:
            if s in row and np.random.rand() > 0.15:
                row[s] = 1
                active_count += 1
                
        # If no typical symptoms were selected, force at least one
        if active_count == 0 and len(typical_symptoms) > 0:
            forced_symptom = np.random.choice(typical_symptoms)
            if forced_symptom in row:
                row[forced_symptom] = 1
                
        # 4. Introduce occasional noise symptoms (random other symptoms, 1% probability)
        for s in SYMPTOMS:
            if s not in typical_symptoms:
                if np.random.rand() < 0.02:
                    row[s] = 1
                    
        # 5. Append label
        row["disease"] = disease
        data.append(row)
        
    df = pd.DataFrame(data)
    df.to_csv("symptoms_dataset.csv", index=False)
    print("Dataset saved to symptoms_dataset.csv.")
    return df

def train_model():
    # Load/generate dataset
    if not os.path.exists("symptoms_dataset.csv"):
        df = generate_synthetic_data()
    else:
        df = pd.read_csv("symptoms_dataset.csv")
        
    # Split features and label
    X = df[SYMPTOMS]
    y = df["disease"]
    
    # Label encoding for targets
    le = LabelEncoder()
    y_encoded = le.fit_transform(y)
    
    # 80/20 train-test split
    X_train, X_test, y_train, y_test = train_test_split(X, y_encoded, test_size=0.2, random_state=42)
    
    print("Training Random Forest Classifier...")
    model = RandomForestClassifier(n_estimators=100, random_state=42)
    model.fit(X_train, y_train)
    
    # Log accuracy
    train_acc = model.score(X_train, y_train)
    test_acc = model.score(X_test, y_test)
    print(f"Training Accuracy: {train_acc:.4f}")
    print(f"Testing Accuracy: {test_acc:.4f}")
    
    # Save model and encoder metadata
    joblib.dump(model, "disease_model.pkl")
    
    # Save the encoder, symptom features, and metadata map
    encoder_data = {
        "classes": le.classes_.tolist(),
        "symptoms": SYMPTOMS,
        "disease_metadata": DISEASE_METADATA
    }
    joblib.dump(encoder_data, "symptoms_encoder.pkl")
    print("Model and encoder assets saved successfully.")

if __name__ == "__main__":
    train_model()
