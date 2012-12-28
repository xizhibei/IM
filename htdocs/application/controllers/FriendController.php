<?php

/**
 * FriendController
 * 
 * @author
 * @version 
 */
require_once 'Zend/Controller/Action.php';
Zend_Loader::loadClass('FriendModel');
Zend_Loader::loadClass('FileModel');
Zend_Loader::loadClass('LocModel');
Zend_Loader::loadClass('UserModel');

class FriendController extends Zend_Controller_Action {

    private $user = null;

    public function init() {
        $acl = Zend_Registry::get("acl");
        $this->user = $acl->getUser();
        $this->_helper->viewRenderer->setNoRender();
        // $this->_helper->layout->disableLayout();
    }

    public function listAction() {
        $response = array();
        $friend = new FriendModel ();
        $f = new FileModel();
        $l = new LocModel();
        $user = new UserModel();
        
        $loc = $user->GetUserLoc($this->user['uid']);
        
        $result = $friend->GetAllMy($this->user ['uid']);
        if ($result != null) {
            $count = 0;
            foreach ($result as $r) {
                if ($r ['uid1'] == $this->user ['uid'])
                    $uid = $r ['uid2'];
                else
                    $uid = $r ['uid1'];
                
                if($loc['latitude'] == 0 && $loc['longtitude'] == 0)
                    $dis = -1;
                else
                    $dis = $l->GetDistance($loc['latitude'] / 1e6, $loc['longitude'] / 1e6, $r ['latitude'] / 1e6, $r ['longitude'] / 1e6);
                array_push($response, array(
                    'uid' => $uid,
                    'type' => $r['type'],
                    'name' => $r ['name'],
                    'sex' => $r ['sex'],
                    'age' => $r['age'],
                    'regdate' => $r ['regdate'],
                    'locupdate' => $r ['updatetime'],
                    'lat' => $r ['latitude'],
                    'lng' => $r ['longitude'],
                    'avatar' => $f->getAvatar($r['avatarid']),
                    'distance' => $dis,
                ));
                $count++;
            }
            $response ['errno'] = 0;
            $response ['count'] = $count;
        } else {
            $response ['errno'] = 1;
        }
        echo json_encode($response);
    }

    public function searchAction() {
        
    }

    public function followAction() {
        $response = array();
        if ($this->getRequest()->isPost()) {
            $data = $this->getRequest()->getPost();
            $friend = new FriendModel ();
            $ret = $friend->follow($this->user ['uid'], $data ['uid']);
            if ($ret == - 1) {
                $response ['errno'] = 1;
            } else
                $response ['errno'] = 0;
        } else {
            $response ['errno'] = 2;
        }
        echo json_encode($response);
    }

}
