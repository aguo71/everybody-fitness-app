import React, { useState } from 'react'; 
import {Tabs, Tab, Form, Col, Row, Card} from 'react-bootstrap';
import './LoginPage.css';

function LoginPage(){
  return (
    <div className="login-page row">
      <div className="login-section login-image">
        <img src="https://static01.nyt.com/images/2020/03/10/well/physed-immune1/physed-immune1-mediumSquareAt3X.jpg" id="login-img" />
      </div>
      <div className="login-section col">
        <div className="login-top">
          <h1>everyBODY</h1>
          <p>Fitness curated by all, for all.</p>
        </div>
        <Card className="login-card" body>
          <Tabs className="login-tabs" defaultActiveKey="login">
            <Tab eventKey="login" title="Login">
              <Form className="login-form">
                <Form.Group controlId="username">
                  <Form.Label>Username</Form.Label>
                  <Form.Control type="text" placeholder="Enter username" />
                </Form.Group>
                <Form.Group controlId="password">
                  <Form.Label>Password</Form.Label>
                  <Form.Control type="password" placeholder="Enter password" />
                </Form.Group>
                <button className="submit-btn" id="login-btn">
                  Login
                </button>
              </Form>
            </Tab>
            <Tab eventKey="signup" title="Sign up">
              <Form className="signup-form">
                <Form.Group controlId="first-name">
                  <Form.Label>First name</Form.Label>
                  <Form.Control type="text" placeholder="Enter first name" />
                </Form.Group>
                <Form.Group controlId="last-name">
                  <Form.Label>Last name</Form.Label>
                  <Form.Control type="text" placeholder="Enter last name" />
                </Form.Group>
                <Form.Group controlId="username">
                  <Form.Label>Username</Form.Label>
                  <Form.Control type="text" placeholder="Enter username" />
                </Form.Group>
                <Form.Group controlId="password">
                  <Form.Label>Password</Form.Label>
                  <Form.Control type="password" placeholder="Enter password" />
                </Form.Group>
                <button className="submit-btn" id="signup-btn">
                  Sign Up
                </button>
              </Form>
            </Tab>
          </Tabs>
        </Card>
      </div>
    </div>
  );
}
export default LoginPage
