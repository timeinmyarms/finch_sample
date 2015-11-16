package com.ok.persistence

import com.ok.model.{Book, User}
import slick.driver.PostgresDriver.api._
import slick.lifted.{TableQuery, Tag}

/**
 * Created by olga.krekhovetska on 06.11.2015.
 */
object Tables {

  class Users(tag: Tag) extends Table[User] (tag, "users"){
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("firstName")
    def lastName = column[String]("lastName")
    def age = column[Option[Int]]("age")
    def version = column[Int]("version")

    def * = (id, firstName, lastName, age, version) <> (User.tupled, User.unapply)
  }

  class Books(tag: Tag) extends Table[Book](tag, "books"){
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def author = column[String]("author")
    def owner = column[Option[Long]]("owner")
    def version = column[Int]("version")

    def * = (id, name, author, owner, version) <> ((Book.apply _).tupled, Book.unapply)
    val ownerFK = foreignKey("books_owner_fkey", owner, users)(_.id)
  }

  object users extends TableQuery(new Users(_)) {
    val usersRetId =  this returning this.map(_.id)

    //THINK: hm..   maybe do not call result on queries for future joins??
    def findById(id: Long) = this.filter(_.id === id).result.headOption
    def findByFirstName(name: String) = this.filter(_.firstName === name).result.headOption
    def findByLastName(name: String) = this.filter(_.lastName === name).result.headOption
    def findByAge(age: Int) = this.filter(_.age === age).result.headOption
    def findAll = this.result
    def update(userToUpdate: User, version: Int) = this.filter(u => u.id === userToUpdate.id && u.version === version).update(userToUpdate)
    def insert(userToInsert: User) = usersRetId += userToInsert
    def delete(id: Long, version: Int) = this.filter(u => u.id === id && u.version === version).delete
  }

  object books extends TableQuery(new Books(_)) {
    val booksRetId =  this returning this.map(_.id)

    def findById(id: Long) = this.filter(_.id === id).result.headOption
    def findByName(name: String) = this.filter(_.name === name).result.headOption
    def findByAuthor(author: String) = this.filter(_.author === author).result.headOption
    def findAll = this.result
    def findByUser(userId: Long) = this.filter(_.owner === userId).result
    def update(bookToUpdate: Book, version: Int) = this.filter(b => b.id === bookToUpdate.id && b.version === version).update(bookToUpdate)
    def insert(bookToInsert: Book) = booksRetId += bookToInsert
    def delete(id: Long, version: Int) = this.filter(b => b.id === id && b.version === version).delete
  }
}
