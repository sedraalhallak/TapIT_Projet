from pydantic import BaseModel

class SongBase(BaseModel):
    title: str
    artist: str

class SongOut(SongBase):
    id: int
    filename: str

    class Config:
        orm_mode = True

class FavoriteOut(SongOut):
    pass