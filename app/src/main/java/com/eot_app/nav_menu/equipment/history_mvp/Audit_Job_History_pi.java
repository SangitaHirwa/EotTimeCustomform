package com.eot_app.nav_menu.equipment.history_mvp;

public interface Audit_Job_History_pi {

    void getEquipmentAduitHistory(String equId);

    void getEquipmentJobHistory(String equId);

    void getEquipmentAduitDetails(String audId);
    void getEqItemFromServer(String equId,String jobId);
    void getEqPartsFromServer(String equId);

    void getEquipmentJobDetails(String audId);

    void getApiForUploadAttchment(String equId, String usrManualDoc);
}
