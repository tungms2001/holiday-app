<?php
class NotificationModel extends Database {
    private $conn;

    public function __construct() {
        $db = new Database();
        $this->conn = $db->connect();
    }

    public function apply($id, $sender, $receiver) {
        $insert_query = "INSERT INTO notification (tour_id, sender, receiver, type) " .
            "VALUES ($id, '$sender', '$receiver', 'apply')";
        $this->conn->query($insert_query);
        return array("success" => true);
    }

    public function load_all($username) {
        $select_query = "SELECT * " .
            "FROM notification " .
            "WHERE receiver = '$username'";
        $result = $this->conn->query($select_query);
        if ($result->num_rows > 0) {
            $notifications = array();
            while ($row = $result->fetch_assoc()) {
                $sender_query = "SELECT fullname, avatar " .
                    "FROM user " .
                    "WHERE user.username = '" . $row['sender'] . "'";
                $sender = $this->conn->query($sender_query)->fetch_assoc();
                $sender_name = $sender['fullname'];
                $sender_avatar = $sender['avatar'];

                $tour_query = "SELECT tour_name " .
                    "FROM tour " .
                    "WHERE tour.id = " . $row['tour_id'];
                $tour_name = $this->conn->query($tour_query)->fetch_assoc()['tour_name'];

                $notification = array(
                    'success' => true,
                    'tour_name' => $tour_name,
                    'sender' => $sender_name,
                    'avatar' => $sender_avatar,
                    'type' => $row['type'],
                    'status' => $row['status'] ?? ''
                );
                array_push($notifications, $notification);
            }
            return $notifications;
        }
        else
            return array('success' => false);
    }
}