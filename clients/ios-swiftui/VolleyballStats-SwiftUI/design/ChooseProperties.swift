//
//  ChooseProperties.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 12/02/2024.
//

import SwiftUI
import shared

struct ChooseProperties<T: AnyObject>: View {
    private let title: String?
    private let checkableProperties: [CheckableProperty<T>]
    private let visible: Bool
    private let onChecked: (T) -> Void
    
    init(choosePropertiesState: ChoosePropertiesState<T>) {
        title = choosePropertiesState.title
        checkableProperties = choosePropertiesState.checkableProperties
        visible = choosePropertiesState.visible
        onChecked = choosePropertiesState.onChecked
    }

    var body: some View {
        if visible {
            VStack(spacing: Dimens.marginMedium) {
                if let title = title {
                    Text(title)
                        .font(.title2)
                        .frame(maxWidth: .infinity, alignment: .leading)
                }
                ForEach(checkableProperties, id: \.self) { property in
                    CheckablePropertyItem(property: property, onChecked: onChecked)
                    Divider()
                }
            }
            .padding()
        }
    }
}

struct CheckablePropertyItem<T: AnyObject>: View {
    var property: CheckableProperty<T>
    var onChecked: (T) -> Void

    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: Dimens.marginMedium) {
                Text(property.title)
                    .font(.title2)
                    .opacity(property.checked ? 1.0 : 0.3)
                Text(property.description_)
                    .font(.body)
                    .opacity(property.checked ? 1.0 : 0.3)
            }
            Spacer()
            if !property.mandatory {
                Image(systemName: property.checked ? "checkmark.square" : "square")
                    .foregroundColor(.accentColor)
            }
        }
        .background(Rectangle().fill(.background))
        .onTapGesture {
            if !property.mandatory {
                onChecked(property.id)
            }
        }
    }
}
