package slick.bulkinsert

import java.sql.Date

case class UserDataModel(
  id: Int,
  name: Option[String],
  info: UserInfoDataModel,
  createdAt: Date
)

case class UserInfoDataModel(
  height: Double,
  weight: Double
)

object UserDataModel {
  def createDataModels(n: Int): Seq[UserDataModel] = {
    val dm = UserDataModel(
      id = 1,
      name = Some("foo"),
      info = UserInfoDataModel(
        height = 180.2,
        weight = 77.7
      ),
      createdAt = Date.valueOf("2022-04-10")
    )
    (1 to n).map(id => dm.copy(id = id, name = if (id % 2 == 0) None else dm.name))
  }
}
