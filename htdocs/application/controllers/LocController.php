<?php

/**
 * LocationController
 * 
 * @author
 * @version 
 */
require_once 'Zend/Controller/Action.php';
Zend_Loader::loadClass("LocModel");
Zend_Loader::loadClass('FileModel');
Zend_Loader::loadClass('FriendModel');

class LocController extends Zend_Controller_Action {

    private $user;

    public function init() {
        $acl = Zend_Registry::get("acl");
        $this->user = $acl->getUser();
        $this->_helper->viewRenderer->setNoRender();
    }

    public function indexAction() {
        
    }

    public function updateAction() {
        $response = array();
        if ($this->getRequest()->isPost()) {
            $data = $this->getRequest()->getPost();

            $latitude = $data ['latitude'];
            $longitude = $data ['longitude'];
            $loc = new LocModel ();

            $loc->UpdateNewLocation($this->user ['uid'], $latitude, $longitude);
            $response ['errno'] = 0;
        } else
            $response ['errno'] = 1;

        echo json_encode($response);
    }

    public function clusterAction() {
        $data = null;
        $loc = new LocModel ();
        if ($this->getRequest()->isPost()) {
            $data = $this->getRequest()->getPost();
            $cluster = $loc->cluster(10, $data['ltLat'], $data['ltLng'], $data['rbLat'], $data['rbLng'], $data['gender'], $data['ageLevel'], $data['updatetime']);
        } else {
            $cluster = $loc->cluster(10);
        }

        $response = array();
        if ($cluster != null) {
            foreach ($cluster as $c) {
                $count = count($c ['sub']);
                if ($count > 0)
                    array_push($response, array(
                        'lat' => intval($c ['lat'] * 1e6),
                        'lng' => intval($c ['lng'] * 1e6),
                        'radix' => $count
                    ));
            }
            $response['count'] = count($response);
            $response['errno'] = 0;
        }else{
            $response['errno'] = 1;
        }
        echo json_encode($response);
    }

    public function nearbyAction() {
        $response = array();
        $loc = new LocModel ();
        $f = new FileModel();
        $friend = new FriendModel();

        $data = $loc->getAdapter()->fetchRow("select * from user where uid = " . $this->user ['uid']);
        if ($data != null) {
            $response ['errno'] = 0;
            $people = $loc->NearbyPeople($this->user ['uid'], $data ['latitude'], $data ['longitude']);
            $response ['count'] = count($people);
            foreach ($people as $p) {
                array_push($response, array(
                    'uid' => $p ['uid'],
                    'type' => $friend->getFriendType($this->user['uid'], $p ['uid']),
                    'name' => $p ['name'],
                    'sex' => $p ['sex'],
                    'age' => $p['age'],
                    'regdate' => $p ['regdate'],
                    'locupdate' => $p ['updatetime'],
                    'lat' => $p ['latitude'],
                    'lng' => $p ['longitude'],
                    'avatar' => $f->getAvatar($p['avatarid']),
                    'distance' => $p['distance'],
                ));
            }
        } else {
            $response ['errno'] = 1;
        }
        echo json_encode($response);
    }

}
