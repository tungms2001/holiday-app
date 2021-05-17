<?php
class TourModel extends Database {
    private $conn;

    public function __construct() {
        $db = new Database();
        $this->conn = $db->connect();
    }

    public function create($creator, $tour_name, $type, $status, $departure, $destination, $during, $members, $note, $image) {
        $bitmap_data = base64_decode($image);
        $img = imagecreatefromstring($bitmap_data);
        $img_path = 'data/img/tour/' . hash('md5', $bitmap_data) . '.png';
        imagepng($img, $img_path);

        $id_query = "SELECT AUTO_INCREMENT " .
            "FROM  INFORMATION_SCHEMA.TABLES " .
            "WHERE TABLE_SCHEMA = 'holidayapp' AND TABLE_NAME = 'tour'";
        $id = $this->conn->query($id_query)->fetch_assoc()['AUTO_INCREMENT'];

        $tour_query = "INSERT INTO tour " .
            "(creator, tour_name, type, status, departure, destination, during, note, image, created_at, updated_at)" .
            "VALUES ('$creator', '$tour_name', '$type', '$status', '$departure', '$destination', '$during', '$note', '$img_path', NOW(), NOW())";
        $this->conn->query($tour_query);

        $list_users = explode(' ', $members);
        array_push($list_users, $creator);
        foreach ($list_users as $item) {
            $member_query = "INSERT INTO member (tour_id, user) VALUES ($id, '$item')";
            $this->conn->query($member_query);
        }

        return array('success' => true, 'message' => "Tour created successfully!");
    }

    public function load_all() {
        $select_query = "SELECT tour_name, type, status, during, image FROM tour";
        $result = $this->conn->query($select_query);

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

    public function load_my_tours($username) {
        $select_query = "SELECT tour_name, type, status, during, image " .
            "FROM tour, member " .
            "WHERE tour.id = member.tour_id AND " .
            "member.user = '$username'";
        $result = $this->conn->query($select_query);

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

    public function load_by_position($position) {
        $select_query = "SELECT * FROM tour ORDER BY id LIMIT $position,1";
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
                'note' => $row['note'],
            );
            return $tour;
        }
        else
            return array("success" => false);
    }

    public function comment($tour_id, $username, $content) {
        $insert_query = "INSERT INTO tour_comment (user, tour_id, content, created_at, deleted_at) " .
            "VALUES ('$username', '$tour_id', '$content', NOW(), NOW())";
        $this->conn->query($insert_query);
        return array("success" => true);
    }

    public function load_comments($id) {
        $select_query = "SELECT tour_comment.tour_id, user.fullname, user.avatar, tour_comment.content " .
            "FROM tour_comment, user " .
            "WHERE tour_comment.user = user.username AND " .
            "tour_comment.tour_id = $id " .
            "ORDER BY tour_comment.tour_id";
        $result = $this->conn->query($select_query);
        $comments = array();
        while ($row = $result->fetch_assoc()) {
            $comment = array(
                'fullname' => $row['fullname'],
                'avatar' => $row['avatar'],
                'content' => $row['content']
            );
            array_push($comments, $comment);
        }
        return $comments;
    }
}