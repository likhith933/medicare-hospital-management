const COMMON_SYMPTOMS = [
    "itching", "skin_rash", "continuous_sneezing", "shivering", "chills", "joint_pain",
    "stomach_pain", "acidity", "ulcers_on_tongue", "vomiting", "burning_micturition", "fatigue",
    "weight_gain", "anxiety", "cold_hands_and_feets", "mood_swings", "weight_loss", "restlessness",
    "lethargy", "patches_in_throat", "cough", "high_fever", "breathlessness", "sweating", "dehydration",
    "indigestion", "headache", "yellowish_skin", "dark_urine", "nausea", "loss_of_appetite", "pain_behind_the_eyes",
    "back_pain", "constipation", "abdominal_pain", "diarrhoea", "mild_fever", "yellowing_of_eyes", "swelled_lymph_nodes",
    "malaise", "blurred_and_distorted_vision", "phlegm", "throat_irritation", "redness_of_eyes", "runny_nose", "congestion",
    "chest_pain", "weakness_in_limbs", "fast_heart_rate", "dizziness", "cramps", "bruising", "obesity", "muscle_pain"
];

const Predictor = {
    selectedSymptoms: [],
    
    renderChips(containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;
        
        container.innerHTML = COMMON_SYMPTOMS.map(sym => {
            const label = sym.replace(/_/g, ' ');
            return `<div class="symptom-chip" data-value="${sym}" onclick="Predictor.toggleSymptom(this, '${sym}')">${label}</div>`;
        }).join('');
    },
    
    toggleSymptom(element, sym) {
        if (element.classList.contains('selected')) {
            element.classList.remove('selected');
            this.selectedSymptoms = this.selectedSymptoms.filter(item => item !== sym);
        } else {
            element.classList.add('selected');
            this.selectedSymptoms.push(sym);
        }
    },
    
    async predict(resultContainerId, docListContainerId) {
        if (this.selectedSymptoms.length === 0) {
            showToast('Please select at least one symptom.', 'warning');
            return;
        }
        
        const resultContainer = document.getElementById(resultContainerId);
        const docListContainer = document.getElementById(docListContainerId);
        
        resultContainer.style.display = 'none';
        if (docListContainer) docListContainer.innerHTML = '';
        
        try {
            const res = await API.post('/api/predict/disease', { symptoms: this.selectedSymptoms });
            if (res.success && res.data) {
                const data = res.data;
                
                // Show result card
                resultContainer.style.display = 'block';
                resultContainer.innerHTML = `
                    <div class="result-header">
                        <h3>Predicted: ${data.disease}</h3>
                        <span class="badge badge-${data.severity.toLowerCase() === 'high' ? 'cancelled' : (data.severity.toLowerCase() === 'medium' ? 'pending' : 'confirmed')}">${data.severity} Severity</span>
                    </div>
                    <div class="result-item">
                        <span class="result-label">Confidence Score:</span> ${(data.confidence * 100).toFixed(0)}%
                    </div>
                    <div class="result-item">
                        <span class="result-label">Recommended Specialist:</span> ${data.specialization}
                    </div>
                    <div class="result-item">
                        <span class="result-label">Suggested Precautions:</span>
                        <div>
                            ${data.precautions.map(p => `<span class="precaution-badge">${p}</span>`).join('')}
                        </div>
                    </div>
                `;
                
                // Fetch doctors matching specialization
                if (docListContainer) {
                    this.loadRecommendedDoctors(data.specialization, docListContainerId);
                }
            }
        } catch (err) {
            console.error('Symptom prediction action failed', err);
        }
    },
    
    async loadRecommendedDoctors(specialization, containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;
        
        container.innerHTML = '<p>Searching for specialists...</p>';
        
        try {
            const res = await API.get(`/api/doctors/${specialization}`);
            if (res.success && res.data) {
                const doctors = res.data;
                if (doctors.length === 0) {
                    container.innerHTML = `<p style="margin-top:1rem; color:var(--text-secondary);">No doctors specialized in ${specialization} are currently available.</p>`;
                    return;
                }
                
                container.innerHTML = `
                    <h4 style="margin: 1.5rem 0 1rem 0; text-align: left; font-family:'Outfit';">Available Specialists:</h4>
                    <div class="dashboard-grid" style="margin-top:0;">
                        ${doctors.map(doc => `
                            <div class="glass-card" style="padding:1.5rem; text-align:left;">
                                <h4>Dr. ${doc.name}</h4>
                                <p style="color:var(--text-secondary); font-size:0.9rem;">Specialization: ${doc.specialization}</p>
                                <p style="color:var(--text-secondary); font-size:0.9rem;">Experience: ${doc.experience} Years</p>
                                <p style="color:var(--accent); font-weight:600; margin:0.5rem 0;">Rating: ★ ${doc.rating}</p>
                                <button class="btn btn-primary" style="width:100%; margin-top:0.75rem; padding:0.5rem; font-size:0.85rem;" onclick="redirectToBooking(${doc.id})">Book Appointment</button>
                            </div>
                        `).join('')}
                    </div>
                `;
            }
        } catch (err) {
            container.innerHTML = '<p style="color:var(--danger)">Error finding recommended doctors.</p>';
        }
    }
};
