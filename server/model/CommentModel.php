<?php
class CommentModel extends Database {
    private $conn;

    public function __construct() {
        $db = new Database();
        $this->conn = $db->connect();
    }

    public function create($tour_id, $username, $content) {
        $insert_query = "INSERT INTO tour_comment (user, tour_id, content, created_at) " .
            "VALUES ('$username', '$tour_id', '$content', NOW())";
        $this->conn->query($insert_query);
        return array("success" => true);
    }

    public function load_all($tour_id) {
        $select_query = "SELECT tour_comment.tour_id, user.fullname, user.avatar, tour_comment.content " .
            "FROM tour_comment, user " .
            "WHERE tour_comment.user = user.username AND " .
            "tour_comment.tour_id = $tour_id " .
            "ORDER BY tour_comment.id";
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