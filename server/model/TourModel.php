<?php
class TourModel extends Database {
    private $conn;

    public function __construct() {
        $db = new Database();
        $this->conn = $db->connect();
    }

    public function create($creator, $tour_name, $type, $status, $departure, $destination, $during, $members, $note, $image) {
        $img_path = $this->store_image($image);
        $id_query = "SELECT AUTO_INCREMENT " .
            "FROM  INFORMATION_SCHEMA.TABLES " .
            "WHERE TABLE_SCHEMA = 'holidayapp' AND TABLE_NAME = 'tour'";
        $id = $this->conn->query($id_query)->fetch_assoc()['AUTO_INCREMENT'];

        $tour_query = "INSERT INTO tour " .
            "(creator, tour_name, type, status, departure, destination, during, note, image, created_at, updated_at)" .
            "VALUES ('$creator', '$tour_name', '$type', '$status', '$departure', '$destination', '$during', '$note', '$img_path', NOW(), NOW())";
        $this->conn->query($tour_query);

        $this->join_tour($id, $members);
        return array('success' => true);
    }

    public function update($id, $tour_name, $type, $status, $departure, $destination, $during, $members, $note, $image) {
        $img_path = $this->store_image($image);
        $update_tour_query = "UPDATE tour " .
            "SET tour_name = '$tour_name', " .
            "type = '$type', " .
            "status = '$status', " .
            "departure = '$departure', " .
            "destination = '$destination', " .
            "during = '$during', " .
            "note = '$note', " .
            "image = '$img_path', " .
            "updated_at = NOW() " .
            "WHERE id = $id";
        $this->conn->query($update_tour_query);

        $reset_members_query = "DELETE FROM member WHERE tour_id = $id";
        $this->conn->query($reset_members_query);

        $this->join_tour($id, $members);
        return array('success' => true);
    }

    public function accept($username, $tour_id) {
        $accept_member_query = "INSERT member (tour_id, user)" .
            "VALUES ($tour_id, '$username')";
        $this->conn->query($accept_member_query);

        $accept_notification_query = "UPDATE notification " .
            "SET status = 'joined' " .
            "WHERE tour_id = $tour_id AND sender = '$username'";
        $this->conn->query($accept_notification_query);
        return array("success" => true, "acp" => $accept_notification_query);
    }

    public function load_all() {
        $select_query = "SELECT tour_name, type, status, during, image FROM tour ORDER BY id DESC";
        $result = $this->conn->query($select_query);
        return $this->to_tours_array($result);
    }

    public function load_by_username($username) {
        $select_query = "SELECT tour_name, type, status, during, image " .
            "FROM tour, member " .
            "WHERE tour.id = member.tour_id AND " .
            "member.user = '$username'";
        $result = $this->conn->query($select_query);
        return $this->to_tours_array($result);
    }

    public function search($keyword) {
        $search_query = "SELECT tour_name, type, status, during, image " .
            "FROM tour " .
            "WHERE MATCH (tour_name, departure, destination, note) " .
            "AGAINST ('$keyword') " .
            "ORDER BY id DESC";
        $result = $this->conn->query($search_query);
        return $this->to_tours_array($result);
    }

    public function load_by_position($position, $keyword, $username) {
        if (empty($keyword))
            $select_query = "SELECT * FROM tour ORDER BY id DESC LIMIT $position,1";
        else
            $select_query = "SELECT * " .
                "FROM tour " .
                "WHERE MATCH (tour_name, departure, destination, note) " .
                "AGAINST ('$keyword') " .
                "ORDER BY id DESC " .
                "LIMIT $position,1";
        $result = $this->conn->query($select_query);
        if ($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            $tour = array(
                'success' => true,
                'id' => $row['id'],
                'tour_name' => $row['tour_name'],
                'type' => $row['type'],
                'departure' => $row['departure'],
                'destination' => $row['destination'],
                'creator' => $row['creator'],
                'status' => $row['status'],
                'during' => $row['during'],
                'image' => $row['image'],
                'note' => $row['note']
            );

            $get_status_query = "SELECT status " .
                "FROM notification " .
                "WHERE tour_id = " . $row['id'] . " AND " .
                "sender = '$username'";
            $status_result = $this->conn->query($get_status_query);
            if ($status_result->num_rows > 0)
                $status = $status_result->fetch_assoc()['status'];
            $tour['notification_status'] = $status ?? '';

            $get_members_query = "SELECT username, avatar " .
                "FROM user, member " .
                "WHERE user.username = member.user AND " .
                "member.tour_id = " . $row['id'];
            $result = $this->conn->query($get_members_query);
            $members = array();
            if ($result->num_rows > 0) {
                while ($row = $result->fetch_assoc()) {
                    $member = array(
                        'username' => $row['username'],
                        'avatar' => $row['avatar']
                    );
                    array_push($members, $member);
                }
            }
            $tour['members'] = $members;
            return $tour;
        }
        else
            return array('success' => false);
    }

    // insert username and tour_id into `member`
    private function join_tour($id, $members) {
        $list_users = explode(' ', $members);
        foreach ($list_users as $item) {
            $member_query = "INSERT INTO member (tour_id, user) VALUES ($id, '$item')";
            $this->conn->query($member_query);
        }
    }

    // explode mysql result table into php array
    private function to_tours_array($result) {
        $tours = array();
        while ($row = $result->fetch_assoc()) {
            $tour = array(
                'tour_name' => $row['tour_name'],
                'type' => $row['type'],
                'status' => $row['status'],
                'during' => $row['during'],
                'image' => $row['image']
            );
            array_push($tours, $tour);
        }
        return $tours;
    }

    // store image path in database
    private function store_image($image) {
        $bitmap_data = base64_decode($image);
        $img = imagecreatefromstring($bitmap_data);
        $img_path = 'data/img/tour/' . hash('md5', $bitmap_data) . '.png';
        imagepng($img, $img_path);
        imagedestroy($img);
        return $img_path;
    }
}