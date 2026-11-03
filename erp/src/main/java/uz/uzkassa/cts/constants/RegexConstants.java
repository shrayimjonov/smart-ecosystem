package uz.uzkassa.cts.constants;

public interface RegexConstants {
   String BANK_SECRET_KEY = "^[A-Z]{1,10}$";
   String CLASS_NAME_DOT_FORMAT = "(?<!^)([A-Z])";
   String EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
   String PHONE = "^\\+?998(20|33|36|50|55|61|62|65|66|67|69|70|71|72|73|74|75|76|77|78|79|88|90|91|93|94|95|97|98|99)[0-9]{7}$";
   String LOGIN = "^[_.@A-Za-z0-9-]*$";
}
