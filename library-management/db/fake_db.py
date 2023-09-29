from faker import Faker
import mysql.connector
import bcrypt
import json
from datetime import datetime

with open("db/config.json") as f:
    config = json.load(f)

conn = mysql.connector.connect(
    host=config["host"],
    user=config["user"],
    password=config["password"],
    database=config["database"]
)

cursor = conn.cursor()
fake = Faker()

quantity = 100
genres = ["Fiction", "Non-fiction", "Mystery", "Science Fiction", "Fantasy",
          "Romance", "Drama", "Adventure", "Biography", "History", "Poetry"]
passwords = []
data = []

# insert users
for i in range(0, quantity):
    id = fake.email()
    password = id[0]
    salt = bcrypt.gensalt(prefix=b"2a")
    hashed_password = bcrypt.hashpw(password.encode("utf-8"), salt)
    firstname = fake.first_name()
    lastname = fake.last_name()
    role = "USER"
    values = (id, hashed_password, firstname, lastname, role, salt)
    data.append(values)
    print(values)
cursor.executemany("INSERT INTO users VALUES (%s, %s, %s, %s, %s, %s)", data)

# insert genres
data.clear()
for i in range(0, len(genres)):
    name = genres[i]
    values = (None, name)
    data.append(values)
    print(values)
cursor.executemany("INSERT INTO genres VALUES (%s, %s)", data)

# insert books
data.clear()
for i in range(0, quantity):
    id = fake.isbn13()
    title = fake.sentence()
    author = fake.name()
    genre_id = fake.random_int(min=1, max=len(genres))
    year = fake.random_int(min=2000, max=datetime.now().year)
    quantity = fake.random_int(min=0, max=10)
    values = (id, title, author, year, quantity, genre_id)
    data.append(values)
    print(values)
cursor.executemany("INSERT INTO books VALUES (%s, %s, %s, %s, %s, %s)", data)

conn.commit()
print("Done")
