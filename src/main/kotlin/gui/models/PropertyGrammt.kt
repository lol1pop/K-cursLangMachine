package gui.models

import gui.controllers.PropertyGrammController
import gui.controllers.Store
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.Priority
import tornadofx.*
import java.util.*

class PropertyGramm(key: String, value: String) {
    fun valueProperty() = getProperty(PropertyGramm::value)
    var value by property(value)

    fun keyProperty() = getProperty(PropertyGramm::key)
    var key by property(key)

}


class  PropertyGrammModel: ItemViewModel<PropertyGramm>() {
    val key = bind { item?.keyProperty() }
    val value = bind { item?.valueProperty() }
}

class PropertyGrammTable: View() {
    val propsCtrl: PropertyGrammController by inject()
    val model: PropertyGrammModel by inject()

    override val root = tableview(propsCtrl.props) {
        prefHeight = 230.0
        isEditable = true
        column("VN", PropertyGramm::key).makeEditable().fixedWidth(50)
        column("PROPS", PropertyGramm::value).makeEditable()
        columnResizePolicy = SmartResize.POLICY
        bindSelected(model)
    }
}

class PropertyGrammList: View() {
    val propsCtrl: PropertyGrammController by inject()
    val store: Store by inject()

    override val root = listview(store.props){
        isEditable = true
        cellFragment(PropertyGrammListCellFragment::class)
    }
}

class PropertyGrammListCellFragment: ListCellFragment<PropertyGrammItem>() {
    val store: Store by inject()
    val props = PropertyGrammItemModel(itemProperty)

    override val root = hbox{
        textfield {
            hgrow = Priority.ALWAYS
            removeWhen { editingProperty.not() }
            whenVisible { requestFocus() }
        }

        button {
            removeWhen { parent.hoverProperty().not().or(editingProperty.not()) }
            setOnAction { store.removedProps(item) }
        }
    }

}

class PropertyGrammItem(value: String){
    val id = UUID.randomUUID()

    val propsProperty = SimpleStringProperty(value)
    var props by propsProperty

    val keyPropsProperty = SimpleStringProperty()
    var keyProps by keyPropsProperty

}

class PropertyGrammItemModel(property: ObjectProperty<PropertyGrammItem>) : ItemViewModel<PropertyGrammItem>(itemProperty = property) {
    val props = bind { item?.propsProperty }
    val keyProps = bind { item?.keyPropsProperty }

}