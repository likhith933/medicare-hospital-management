const Appointments = {
    async loadPatientAppointments(patientId, containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;
        
        container.innerHTML = '<tr><td colspan="6">Loading appointments...</td></tr>';
        
        try {
            const res = await API.get(`/api/appointments/patient/${patientId}`);
            if (res.success && res.data) {
                const list = res.data;
                if (list.length === 0) {
                    container.innerHTML = '<tr><td colspan="6">No appointments booked yet.</td></tr>';
                    return;
                }
                
                container.innerHTML = list.map(app => `
                    <tr>
                        <td>#${app.id}</td>
                        <td>Dr. ${app.doctor.name} (${app.doctor.specialization})</td>
                        <td>${app.appointmentDate}</td>
                        <td>${app.timeSlot}</td>
                        <td><span class="badge badge-${app.status.toLowerCase()}">${app.status}</span></td>
                        <td>
                            ${app.status === 'PENDING' || app.status === 'CONFIRMED' ? `
                                <button class="btn btn-secondary" style="padding:0.4rem 0.8rem; font-size:0.8rem; margin-right:0.25rem;" onclick="openRescheduleModal(${app.id})">Reschedule</button>
                                <button class="btn btn-danger" style="padding:0.4rem 0.8rem; font-size:0.8rem;" onclick="Appointments.cancel(${app.id}, () => location.reload())">Cancel</button>
                            ` : '-'}
                        </td>
                    </tr>
                `).join('');
            }
        } catch (err) {
            container.innerHTML = '<tr><td colspan="6" style="color:var(--danger)">Error loading appointments.</td></tr>';
        }
    },
    
    async loadDoctorAppointments(doctorId, containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;
        
        container.innerHTML = '<tr><td colspan="6">Loading appointments...</td></tr>';
        
        try {
            const res = await API.get(`/api/appointments/doctor/${doctorId}`);
            if (res.success && res.data) {
                const list = res.data;
                if (list.length === 0) {
                    container.innerHTML = '<tr><td colspan="6">No appointments assigned.</td></tr>';
                    return;
                }
                
                container.innerHTML = list.map(app => `
                    <tr>
                        <td>#${app.id}</td>
                        <td>${app.patient.name} (Age: ${app.patient.age})</td>
                        <td>${app.appointmentDate}</td>
                        <td>${app.timeSlot}</td>
                        <td><span class="badge badge-${app.status.toLowerCase()}">${app.status}</span></td>
                        <td>
                            ${app.status === 'PENDING' ? `
                                <button class="btn btn-primary" style="padding:0.4rem 0.8rem; font-size:0.8rem; margin-right:0.25rem;" onclick="confirmAppointment(${app.id})">Confirm</button>
                                <button class="btn btn-danger" style="padding:0.4rem 0.8rem; font-size:0.8rem;" onclick="Appointments.cancel(${app.id}, () => location.reload())">Cancel</button>
                            ` : ''}
                            ${app.status === 'CONFIRMED' ? `
                                <button class="btn btn-primary" style="padding:0.4rem 0.8rem; font-size:0.8rem;" onclick="openCompleteModal(${app.id}, ${app.patient.id})">Complete</button>
                            ` : ''}
                            ${app.status === 'COMPLETED' || app.status === 'CANCELLED' ? '-' : ''}
                        </td>
                    </tr>
                `).join('');
            }
        } catch (err) {
            container.innerHTML = '<tr><td colspan="6" style="color:var(--danger)">Error loading appointments.</td></tr>';
        }
    },
    
    async cancel(id, callback) {
        if (!confirm('Are you sure you want to cancel this appointment?')) return;
        try {
            const res = await API.put(`/api/appointments/cancel/${id}`);
            if (res.success) {
                showToast('Appointment cancelled successfully', 'success');
                if (callback) callback();
            }
        } catch (err) {
            console.error('Cancellation submission action failed', err);
        }
    },
    
    async confirm(id, callback) {
        try {
            const res = await API.put(`/api/appointments/confirm/${id}`);
            if (res.success) {
                showToast('Appointment confirmed successfully', 'success');
                if (callback) callback();
            }
        } catch (err) {
            console.error('Confirmation action failed', err);
        }
    },

    async book(patientId, doctorId, date, slot, symptoms, notes) {
        try {
            const res = await API.post('/api/appointments/book', {
                patientId,
                doctorId,
                appointmentDate: date,
                timeSlot: slot,
                symptoms,
                notes
            });
            if (res.success) {
                showToast('Appointment booked successfully!', 'success');
                setTimeout(() => {
                    window.location.href = 'patient-dashboard.html';
                }, 1500);
            }
        } catch (err) {
            console.error('Booking submission action failed', err);
        }
    },
    
    async reschedule(id, newDate, newSlot, callback) {
        try {
            const res = await API.put(`/api/appointments/reschedule/${id}`, {
                appointmentDate: newDate,
                timeSlot: newSlot
            });
            if (res.success) {
                showToast('Appointment rescheduled successfully!', 'success');
                if (callback) callback();
            }
        } catch (err) {
            console.error('Rescheduling submission action failed', err);
        }
    },

    async complete(id, patientId, doctorId, diagnosis, prescription, labReports, callback) {
        try {
            // 1. Create medical record entry
            const recordRes = await API.post('/api/medical-records/add', {
                patientId,
                doctorId,
                diagnosis,
                prescription,
                labReports,
                visitDate: new Date().toISOString().split('T')[0]
            });
            
            if (recordRes.success) {
                // 2. Complete appointment state
                const appRes = await API.put(`/api/appointments/complete/${id}`);
                if (appRes.success) {
                    showToast('Appointment completed and medical record generated.', 'success');
                    if (callback) callback();
                }
            }
        } catch (err) {
            console.error('Completion process failed', err);
        }
    }
};
