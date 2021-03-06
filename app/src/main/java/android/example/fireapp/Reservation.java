package android.example.fireapp;

/**
 * This class is the outline of the reservation object which is created when a reservation is made
 *@date 14.05.2020
 *@author Group 3C
 */
public class Reservation {

    // properties
    String reservID, cusID, restaurantID, restaurantName, cusName, restaurantPhone, cusPhone,
            preOrder, date, timeSlot, totalPrice, seat, preOrderTxt;
    boolean hasRated;

    // constructors

    // Empty constructor required for firebase
    public Reservation() { }

    public Reservation(String reservID, String cusID, String restaurantID, String cusName,
                       String restaurantName, String cusPhone, String restaurantPhone,
                       String preOrder, String date, String timeSlot, String totalPrice, String seat) {
        this.reservID = reservID;
        this.cusID = cusID;
        this.restaurantID = restaurantID;
        this.restaurantName = restaurantName;
        this.cusName = cusName;
        this.restaurantPhone = restaurantPhone;
        this.cusPhone = cusPhone;
        this.preOrder = preOrder;
        this.date = date;
        this.timeSlot = timeSlot;
        this.totalPrice = totalPrice;
        this.seat = seat;
        hasRated = false;
        preOrderTxt = "";
    }

    // methods

    // GET & SET METHODS

    public String getReservID() {
        return reservID;
    }

    public void setReservID(String reservID) {
        this.reservID = reservID;
    }

    public String getCusID() {
        return cusID;
    }

    public void setCusID(String cusID) {
        this.cusID = cusID;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getCusName() {
        return cusName;
    }

    public void setCusName(String cusName) {
        this.cusName = cusName;
    }

    public String getRestaurantPhone() {
        return restaurantPhone;
    }

    public void setRestaurantPhone(String restaurantPhone) {
        this.restaurantPhone = restaurantPhone;
    }

    public String getCusPhone() {
        return cusPhone;
    }

    public void setCusPhone(String cusPhone) {
        this.cusPhone = cusPhone;
    }

    public String getPreOrder() {
        return preOrder;
    }

    public void setPreOrder(String preOrder) {
        this.preOrder = preOrder;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getPreOrderTxt() {
        return preOrderTxt;
    }

    public void setPreOrderTxt(String preOrderTxt) {
        this.preOrderTxt = preOrderTxt;
    }

    public boolean isHasRated() {
        return hasRated;
    }

    public void setHasRated(boolean hasRated) {
        this.hasRated = hasRated;
    }
}
