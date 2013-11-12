package com.pokware.jb;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.pokware.jb.objects.Climber;
import com.pokware.jb.objects.GameObject;
import com.pokware.jb.objects.CollisionCategory;
import com.pokware.jb.objects.GameObjectData;
import com.pokware.jb.objects.Jack;
import com.pokware.jb.objects.LevelObjectManager;

public final class CollisionManager implements ContactFilter, ContactListener {

	public LevelObjectManager objectManager;

	public CollisionManager(LevelObjectManager objectManager) {
		this.objectManager = objectManager;
	}

	@Override
	public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
		final Body bodyA = fixtureA.getBody();
		final Body bodyB = fixtureB.getBody();
		GameObjectData userDataA = (GameObjectData) bodyA.getUserData();
		GameObjectData userDataB = (GameObjectData) bodyB.getUserData();
		CollisionCategory categoryA = userDataA.collisionCategory;
		CollisionCategory categoryB = userDataB.collisionCategory;
		
		// Disable jack-platform collision when climbing
		if ((categoryA == CollisionCategory.JACK && categoryB == CollisionCategory.PLATFORM) 
				|| (categoryB == CollisionCategory.JACK && categoryA == CollisionCategory.PLATFORM)) {					
			Jack jack = objectManager.getJack();
			return !jack.isClimbing();			
		}
		// Disable enemy-platform collision when climbing
		else if (categoryA == CollisionCategory.ENEMY && categoryB == CollisionCategory.PLATFORM) {
			GameObject gameObject = objectManager.get(userDataA.id);
			if (gameObject instanceof Climber) {
				return !((Climber)gameObject).isClimbing();
			}
			return true;
		}
		else if (categoryB == CollisionCategory.ENEMY && categoryA == CollisionCategory.PLATFORM) {			
			GameObject gameObject = objectManager.get(userDataB.id);
			if (gameObject instanceof Climber) {
				return !((Climber)gameObject).isClimbing();
			}
			return true;			
		}
		
		else if (categoryA == CollisionCategory.ENEMY && categoryB == CollisionCategory.COLLECTABLE) {			
			return false;
		}
		else if (categoryB == CollisionCategory.ENEMY && categoryA == CollisionCategory.COLLECTABLE) {			
			return false;			
		}
		// Disable enemy-enemy collision (always)
		else if (categoryA == CollisionCategory.ENEMY && categoryB == CollisionCategory.ENEMY) {
			return false;
		}
		return true;
	}

	Vector2 curr = new Vector2();
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		Body bodyA = fixtureA.getBody();
		Body bodyB = fixtureB.getBody();
		GameObjectData userDataA = (GameObjectData) bodyA.getUserData();		
		GameObjectData userDataB = (GameObjectData) bodyB.getUserData();
		
		if ((userDataA.collisionCategory == CollisionCategory.JACK && userDataB.collisionCategory == CollisionCategory.PLATFORM)
				|| (userDataB.collisionCategory == CollisionCategory.JACK && userDataA.collisionCategory == CollisionCategory.PLATFORM)) {
			// drop down the the ladder below
			if (objectManager.getJack().wasDraggingDown && objectManager.getJack().getLadderStatus() >= 2) {
				contact.setEnabled(false);
			} else if (objectManager.getJack().isClimbing()) {
				
				contact.setEnabled(false);
			}
		}		
		else if (userDataA.collisionCategory == CollisionCategory.ENEMY && userDataB.collisionCategory == CollisionCategory.PLATFORM) {
			GameObject gameObject = objectManager.get(userDataA.id);			
			if (gameObject instanceof Climber) { 
				if (((Climber) gameObject).isClimbing()) {
					contact.setEnabled(false);
				}
			}
		}
		else if ((userDataB.collisionCategory == CollisionCategory.ENEMY && userDataA.collisionCategory == CollisionCategory.PLATFORM)) {
			GameObject gameObject = objectManager.get(userDataB.id);
			if (gameObject instanceof Climber) {
				if (((Climber) gameObject).isClimbing()) {
					contact.setEnabled(false);
				}
			}
		}		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

	@Override
	public void endContact(Contact contact) {		
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		Body bodyA = fixtureA.getBody();
		Body bodyB = fixtureB.getBody();
		GameObjectData userDataA = (GameObjectData) bodyA.getUserData();
		GameObjectData userDataB = (GameObjectData) bodyB.getUserData();
		
		if (userDataA.collisionCategory == CollisionCategory.PLATFORM && userDataB.collisionCategory != CollisionCategory.PLATFORM) {
			userDataB.flying = true;
		}
		else if (userDataB.collisionCategory == CollisionCategory.PLATFORM && userDataA.collisionCategory != CollisionCategory.PLATFORM) {
			userDataA.flying = true;
		}
	}

	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		Body bodyA = fixtureA.getBody();
		Body bodyB = fixtureB.getBody();
		GameObjectData userDataA = (GameObjectData) bodyA.getUserData();
		GameObjectData userDataB = (GameObjectData) bodyB.getUserData();
		if ((userDataA.collisionCategory == CollisionCategory.JACK && userDataB.collisionCategory == CollisionCategory.ENEMY)) {

//			Art.hurtSound.play();
			Vector2 positionA = bodyA.getPosition();
			Vector2 positionB = bodyB.getPosition();
			Vector2 b2a = positionA.sub(positionB).nor();
			bodyA.applyLinearImpulse(b2a.mul(2000f), new Vector2(32, 32));			
		}
		else if ((userDataB.collisionCategory == CollisionCategory.JACK && userDataA.collisionCategory == CollisionCategory.ENEMY)) {
//			Art.hurtSound.play();
				
			Vector2 positionA = bodyA.getPosition();
			Vector2 positionB = bodyB.getPosition();
			Vector2 b2a = positionA.sub(positionB).nor();
			bodyB.applyLinearImpulse(b2a.rotate(180f).mul(2000f), new Vector2(32, 32));
			
		}				
		else if ((userDataB.collisionCategory == CollisionCategory.JACK && userDataA.collisionCategory == CollisionCategory.COLLECTABLE)) {
//			Art.coinSound.play();
			userDataA.hidden = true;
		}
		else if ((userDataA.collisionCategory == CollisionCategory.JACK && userDataB.collisionCategory == CollisionCategory.COLLECTABLE)) {
//			Art.coinSound.play();
			userDataB.hidden = true;
		}			
		else if (userDataA.collisionCategory == CollisionCategory.PLATFORM && userDataB.collisionCategory != CollisionCategory.PLATFORM) {
			userDataB.flying = false;
		}
		else if (userDataB.collisionCategory == CollisionCategory.PLATFORM && userDataA.collisionCategory != CollisionCategory.PLATFORM) {
			userDataA.flying = false;
		}
	}
}