from sqlalchemy import Column, Integer, String
from database import Base
from sqlalchemy import ForeignKey
from sqlalchemy.orm import relationship

class Song(Base):
    __tablename__ = "songs"

    id = Column(Integer, primary_key=True, index=True)
    title = Column(String)
    artist = Column(String)
    filename = Column(String)
    

class Favorite(Base):
    __tablename__ = "favorites"
    id = Column(Integer, primary_key=True, index=True)
    song_id = Column(Integer, ForeignKey("songs.id"))
    song = relationship("Song")