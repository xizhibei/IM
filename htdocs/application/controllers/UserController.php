<?php

/**
 * UserController
 * 
 * @author
 * @version 
 */
require_once 'Zend/Controller/Action.php';

Zend_Loader::loadClass('UserModel');
Zend_Loader::loadClass('FileModel');
Zend_Loader::loadClass('LocModel');
Zend_Loader::loadClass('FriendModel');

class UserController extends Zend_Controller_Action {

    private $user;

    public function init() {
        $acl = Zend_Registry::get("acl");
        $this->user = $acl->getUser();
        $this->_helper->viewRenderer->setNoRender();
    }

    public function indexAction() {
// 		$user = new UserModel ();
// 		$result = $user->fetchAll()->toArray();
// 		foreach($result as $r){
// 			$user->update(array('pwd'=>md5($r['name'].$r['salt'])), "uid = " . $r['uid']);
// 		}
//        $user = new UserModel ();
//        $result = $user->getAdapter()->fetchAll("select * from location group by uid having max(updatetime)");
//        foreach ($result as $r) {
//            $user->update(array(
//                'lid' => $r['lid'],
//                    ), "uid = " . $r['uid']);
//        }
        
        $user = new UserModel ();
        $result = $user->getAdapter()->fetchAll("select * from want group by uid having max(d_updatetime)");
        foreach ($result as $r) {
            $user->update(array(
                'wantid' => $r['did'],
                    ), "uid = " . $r['uid']);
        }
//        $result = $user->getAdapter()->fetchAll("select uid from user");
//        $time = new Zend_Date();
//        $time->setHour(0);
//        $time->setMinute(0);
//        $time->setSecond(0);
//        foreach ($result as $r) {
//            $time->setYear(1990 + rand(-20, 20));
//            $time->setMonth(rand(1,12));
//            $time->setDay(rand(1, 28));    
//            $user->update(array(
//                'birthday' => $time->toValue()
//                    ), "uid = " . $r['uid']);
//        }
//        $result = $user->getAdapter()->fetchAll("select birthday from user limit 0,30");
//        $time = new Zend_Date();
//
//        foreach ($result as $r) {
//            $time->set($r['birthday']);
//            echo $time->toString() . "<br>";
//        }
    }

    public function infoAction() {
        $response = array();
        $user = new UserModel ();
        if ($this->getRequest()->isPost()) {
            $data = $this->getRequest()->getPost();
            $uid = $data['uid'];
            $loc = null;
        } else if (isset($this->user['uid'])) {
            $uid = $this->user['uid'];
            $loc = $user->GetUserLoc($uid);
        } else {
            $response ['errno'] = 2;
            echo json_encode($response);
            return;
        }
       
        $p = $user->GetUser($uid);

        if ($p == null)
            $response ['errno'] = 1;
        else {
            $dis = 0;
            if($loc != null){
                $l = new LocModel();
                $dis = $l->GetDistance($loc['latitude'] / 1e6, $loc['longtitude'] / 1e6, $p ['latitude'] / 1e6, $p ['longitude'] / 1e6);
            }
            $f = new FileModel();
            $friend = new FriendModel();
            $response = array(
                'uid' => $p ['uid'],
                'type' => $friend->getFriendType($this->user['uid'], $uid),
                'name' => $p ['name'],
                'sex' => $p ['sex'],
                'age' => $p['age'],
                'regdate' => $p ['regdate'],
                'locupdate' => $p ['locupdatetime'],
                'lat' => $p ['latitude'],
                'lng' => $p ['longitude'],
                'avatar' => $f->getAvatar($p['avatarid']),
                'distance' => $dis,
            );
            $response ['errno'] = 0;
        }
        echo json_encode($response);
    }

    public function regAction() {
        $response = array();
        if ($this->getRequest()->isPost()) {
            $data = $this->getRequest()->getPost();

            $user = new UserModel ();

            if (preg_match('/^[0-9a-z]+@(([0-9a-z]+)[.])+[a-z]{2,3}$/', $data ['email']) == 0) {
                $response ['errno'] = 1;
            } else if ($user->EmailExist($data ['email'])) {
                $response ['errno'] = 2;
            } else {
                $salt = substr(md5(time()), 0, 6);

                $insertData = array(
                    'name' => $data ['name'],
                    'email' => $data ['email'],
                    'pwd' => $user->EncryptPwd($data ['pwd'], $salt),
                    'salt' => $salt,
                    // 'phoneinfo' => $data ['pinfo'],
                    'regdate' => time(),
                    'status' => UserModel::EmailNotValidate
                );

                $id = $user->insert($insertData);
                $response ['errno'] = 0;
                $response ['uid'] = $id;
            }
        } else
            $response ['errno'] = 3;
        echo json_encode($response);
    }

    public function loginAction() {
        $response = array();
        if ($this->getRequest()->isPost()) {
            $data = $this->getRequest()->getPost();

            $user = new UserModel ();
            $result = $user->Validate($data ['email'], $data ['pwd']);
            if ($result == - 2)
                $response ['errno'] = 1;
            else if ($result != - 1) {
                // $acl = $this->_frontController->getPlugin('My_Plugin_Acl');
                $acl = Zend_Registry::get("acl");
                $acl->setUser($result);

                $f = new FileModel();

                $response = array(
                    'errno' => 0,
                    'uid' => $result ['uid'],
                    'name' => $result ['name'],
                    'email' => $result ['email'],
                    'sex' => $result ['sex'],
                    'age' => $result['age'],
                    'regdate' => $result ['regdate'],
                    'birthday' => $result ['birthday'],
                    'locupdate' => $result ['updatetime'],
                    'lat' => $result ['latitude'],
                    'lng' => $result ['longitude'],
                    'avatar' => $f->getAvatar($result['avatarid']),
                    'distance' => 0,
                );
            } else {
                $response ['errno'] = 2;
            }
        } else
            $response ['errno'] = 3;
        echo json_encode($response);
    }

}
