//
//  SelectOptionView.swift
//  VolleyballStats-SwiftUI
//
//  Created by Kamil Halko on 08/02/2024.
//

import SwiftUI
import shared

struct SelectOptionView<T: AnyObject>: View {
    
    private let selectOption: SelectOptionState<T>
    @State private var showingPopover = false
    
    init(selectOption: SelectOptionState<T>) {
        self.selectOption = selectOption
    }
    
    private var formattedSelectedListString: String {
        let formatted = ListFormatter.localizedString(byJoining: selectOption.options.filter { $0.selected }.map { $0.label })
        return formatted.isEmpty ? "-" : formatted
    }
    
    var body: some View {
        HStack {
            if let title = selectOption.title {
                Text(title)
                    .font(.headline)
                Spacer()
            }
            Text(formattedSelectedListString)
                .foregroundColor(selectOption.title != nil ? .gray : .accentColor)
                .multilineTextAlignment(.trailing)
            if selectOption.title == nil {
                Image(systemName: "chevron.up.chevron.down")
//                    .resizable()
//                    .scaleEffect(0.25)
                    .foregroundColor(.accentColor)
            }
        }
        .frame(minHeight: 45)
        .frame(maxWidth: .infinity)
        .background(Rectangle().fill(.background.opacity(selectOption.title != nil ? 1.0 : 0.0)))
        .onTapGesture {
            showingPopover = !showingPopover
        }
        .popover(isPresented: $showingPopover, arrowEdge: .bottom) {
            List {
                ForEach(selectOption.options) { selectable in
                    Button(action: { selectOption.onSelected(selectable.id) }) {
                        HStack {
                            Text(selectable.label)
                            Spacer()
                            if selectable.selected {
                                Image(systemName: "checkmark")
                            }
                        }.padding(.horizontal, Dimens.marginMedium)
                    }.tag(selectable.id)
                        .listRowInsets(.init())
                }
            }.frame(minWidth: 320, minHeight: CGFloat(selectOption.options.count * 45))
                .presentationCompactAdaptation(.popover)
                .listStyle(.inset)
        }
    }
}

extension SelectOptionStateOption : Identifiable {
}

