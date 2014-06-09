package com.tandy.registration;

public enum Subject {
    
    ACCT ("Accounting"),
    AGBE ("Agribusiness Economics"),
    AGCM ("Agricultural Communications"),
    AGED ("Agricultural Education"),
    AGET ("Agricultural Engineering Technology"),
    AGHT ("Horticulture"),
    AGR ("Agriculture"),
    AGRI ("Animal Science - RODP"),//RODP
    AGRN ("Agronomy"),
    ALH ("Allied Health RODP"),//RODP
    ANS ("Animal Science"),
    ARED ("Art Education"),
    ART ("Art"),
    ARTH ("Art - RODP"),//RODP
    ASTL ("Adv Studies-Tching & Lrn"),//RODP
    ASTR ("Astronomy"),
    BIOL ("Biology"),
    BMGT ("Business Management"),
    BUS ("Business - RODP"),//RODP
    CEE ("Civil and Environmental Engineering"),
    CFS ("Child and Family Studies"),
    CHE ("Chemical Engineering"),
    CHEM ("Chemistry"),
    CIS ("Computing - RODP"),//RODP
    CISP ("Computing - RODP"),//RODP
    CJ ("Criminal Justice"),
    CJA ("Criminal Justice - RODP"),//RODP
    CMT ("Communications - RODP"),//RODP
    COBH ("Public Health"),
    COL ("College Exp Online - RODP"),//RODP
    COM ("Visual Communicat - RODP"),//RODP
    COMM ("Communications - RODP"),//RODP
    COMP ("Computer Science - RODP"),//RODP
    COOP ("Cooperative Education"),
    CRMJ ("Crim Justice Admin-RODP"),//RODP
    CSC ("Computer Science"),
    CSCI ("Database Mgt Sys - RODP"),//RODP
    CST ("Computer Sci Tech - RODP"),//RODP
    CTE ("Career Technical Education"),
    CUED ("Curriculum Education"),
    DS ("Decision Sciences"),
    DSPM ("Developmental Math"),
    DSPR ("Developmental Reading"),
    DSPS ("Developmental Study Skills"),
    DSPW ("Developmental Writing"),
    ECE ("Electrical and Computer Engineering"),
    ECED ("Early Childhood Education"),
    ECON ("Economics"),
    ECSP ("Early Childhood Special Education"),
    EDAD ("Education Administration"),
    EDCI ("Education - C & I"),
    EDPY ("Educational Psychology"),
    EDU ("Education"),
    EDUC ("Young Children"),
    EDUL ("Literacy"),
    EDUP ("Program Planning and Evaluation"),
    EDUS ("STEM Education"),
    ELED ("Elementary Education"),
    ELPA ("Edu Ldrshp/Pol Anl - RODP"),//RODP
    ENGL ("English"),
    ENGR ("Engineering"),
    ENTC ("Technical Comm - RODP"),//RODP
    ESC ("Environmental Stu - RODP"),//RODP
    ESL ("English as a Second Language"),
    ESLP ("English as a Second Language Pedagogy"),
    ET ("Technology - RODP"),//RODP
    EVS ("Environmental Science"),
    EVSA ("Environ Science - Agriculture"),
    EVSB ("Environ Science - Biology"),
    EVSC ("Environ Science - Chemistry"),
    EVSG ("Environ Science - Earth Science"),
    EXPW ("Exercise Science, Physical Education and Wellness"),
    FIN ("Finance"),
    FOED ("Foundations of Education"),
    FREN ("Foreign Languages - French"),
    GEOG ("Geography"),
    GEOL ("Geology"),
    GERM ("Foreign Languages - German"),
    HEC ("Human Ecology"),
    HETH ("Health - RODP"),//RODP
    HIST ("History"),
    HIT ("Health Inform Tech - RODP"),//RODP
    HON ("Honors"),
    HPRO ("Human Move Sci- Edu -RODP"),//RODP
    HPSS ("Sports Fitness - RODP"),//RODP
    HSC ("Human Sciences- RODP"),//RODP
    HTL ("Hospitality - RODP"),//RODP
    HUM ("Humanities"),
    INFS ("Information Sys - RODP"),//RODP
    INSL ("Instructional Leadership"),
    INTC ("Computer Graphics - RODP"),//RODP
    JOUR ("Journalism"),
    LAW ("Business Law"),
    LDSP ("Leadership DeveloP - RODP"),//RODP
    LING ("Linguistics"),
    LIST ("Interdisciplinary Studies"),
    LSCI ("Library Science"),
    MATH ("Mathematics"),
    MDT ("Web Development - RODP"),//RODP
    ME ("Mechanical Engineering"),
    MGMT ("Principles of MGMT - RODP"),//RODP
    MIT ("Manufacturing and Industrial Technology"),
    MKT ("Marketing"),
    MS ("Military Science"),
    MSCI ("College of Arts and Sciences"),
    MUED ("Music Education"),
    MUS ("Music"),
    NURS ("Nursing"),
    ORCO ("Organizational Comm -RODP"),//RODP
    PADM ("Public Admin - RODP"),//RODP
    PC ("Professional Communication"),
    PETE ("Psychology of Sports"),
    PHED ("Physical Education"),
    PHIL ("Philosophy"),
    PHYS ("Physics"),
    PM ("Public Management - RODP"),//RODP
    POLI ("Political Science - RODP"),//RODP
    POLS ("Political Science"),
    PRST ("Professional Studies RODP"),//RODP
    PS ("Political Science - RODP"),//RODP
    PSCI ("Physical Science - RODP"),//RODP
    PSY ("Psychology"),
    PSYC (" Psychology - RODP"),//RODP
    PTMA ("Managing Info Tech - RODP"),//RODP
    PY ("Psychology - RODP"),//RODP
    READ ("Reading"),
    SA ("Study Abroad"),
    SEED ("Secondary Education"),
    SOAA ("Social Statistics - RODP"),//RODP
    SOC ("Sociology"),
    SOCI ("Soc & Anthropology - RODP"),//RODP
    SPAN ("Foreign Languages - Spanish"),
    SPCH ("Speech"),
    SPED ("Special Education"),
    SW ("Social Work"),
    TEAE ("Tch/Eng 2nd Lng  RODP"),//RODP
    TEAS ("Tching/Assmnt Proc- RODP"),//RODP
    TECH ("Technology - RODP"),//RODP
    TELC ("Mastrs Tchg License  RODP"),//RODP
    THEA ("Theatre"),
    UBUS ("University Business"),
    UNIV ("University Success"),
    UNMU ("University Music"),
    UNPP ("University Pre-Professional"),
    WEB ("Web Develop Tech - RODP"),//RODP
    WEBD ("Web Design"),
    WFS ("Wildlife and Fisheries Science"),
    WMST ("Women's Studies - RODP");//RODP

    private final String mName;
    
    Subject(String name) {
        mName = name;
    }
    
    public String getName() {
        return mName;
    }
}
