package g2sysnet.smart_gw.m_model

data class ItemInfoEmp_m_orders_emp (
    var CD_DEPT:String,
    var NM_DEPT:String,
    var DEPT:String,
    var CD_EMP:String,
    var NM_EMP:String,
    var NO_LEVEL:String,
    var NO_POS:String,
    var YN_END:String,
    var NO:String
)

//nm_sp=>SP_MOB_GET_DEPT&param=>CD_FIRM
//"CD_DEPT": "1000",
//"NM_DEPT": "영업본부",
//"DEPT": "(1000) 영업본부",
//"CD_EMP": "1101",
//"NM_EMP": "홍길동",
//"NO_LEVEL": "1",
//"NO_POS": "1000001",
//"NM_POS": "팀짱",
//"YN_END": "N",
//"NO": "1"