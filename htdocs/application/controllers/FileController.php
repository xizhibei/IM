<?php
Zend_Loader::loadClass('FileModel');

class FileController extends Zend_Controller_Action {
    private $user;
    
    public function init() {
        $acl = Zend_Registry::get("acl");
        $this->user = $acl->getUser();
        $this->_helper->viewRenderer->setNoRender();
    }

    public function uploadAction() {
        $response = array();
        if (!isset($_FILES['file'])) {
            $response['errno'] = 1;
        } else {
            if ($_FILES["file"]["error"] > 0) {
                $response['errno'] = 1;
                $response['errno_file'] = $_FILES["file"]["error"];
            } else {
//                echo "Upload: " . $_FILES["file"]["name"] . "<br />";
//                echo "Type: " . $_FILES["file"]["type"] . "<br />";
//                echo "Size: " . ($_FILES["file"]["size"] / 1024) . " Kb<br />";
//                echo "Stored in: " . $_FILES["file"]["tmp_name"];
                if($_FILES['file']['type'] == "image/png"){
                    $ex = ".png";
                }else if($_FILES['file']['type'] == "image/jpg" || $_FILES['file']['type'] == "image/jpeg"){
                    $ex = ".jpg";
                }else if($_FILES['file']['type'] == "image/gif"){
                    $ex = ".gif";
                }else if($_FILES['file']['type'] == "audio/amr"){
                    $ex = ".amr";
                }
                
                $filename = md5( basename($_FILES["file"]["name"]) . time() ) . $ex;
                
                move_uploaded_file($_FILES["file"]["tmp_name"],$_SERVER['DOCUMENT_ROOT'] . "/upload/" . $filename);
                
                $f = new FileModel();
                $id = $f->insert(array(
                    'name' => $filename,
                    'time' => time(),
                    'uid' => $this->user['uid'],
                ));
                
                $response['fid'] = $id;
                $response['errno'] = 0;
            }
        }
        
        echo json_encode($response);
    }

}

?>
